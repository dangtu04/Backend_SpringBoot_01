package com.dangthanhtu.example05.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
// import java.util.stream.Collectors;

// import com.dangthanhtu.example05.entity.Product;
import com.dangthanhtu.example05.payloads.ChatResponse;
// import com.dangthanhtu.example05.payloads.ProductDTO;
// import com.dangthanhtu.example05.payloads.ProductResponse;

@Service
public class ChatbotService {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-001:generateContent";

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ProductService productService;

    public ChatbotService(WebClient.Builder webClientBuilder, ProductService productService) {
        this.webClient = webClientBuilder.baseUrl(API_URL).build();
        this.objectMapper = new ObjectMapper();
        this.productService = productService;
    }

    public Mono<ChatResponse> getAiResponse(String userInput) {
        System.out.println("User input: " + userInput);

        // Thử extract tên sản phẩm
        List<String> productNames = extractProductNames(userInput);
        System.out.println("Extracted product names: " + productNames);

        // Nếu phát hiện intent mua sắm, tìm sản phẩm
        if (!productNames.isEmpty()) {
            String keyword = productNames.get(0);
            System.out.println("Searching for keyword: " + keyword);
            return productService.searchProductByKeyword(keyword, 0, 10, "productId", "asc")
                    .map(productResponse -> {
                        if (productResponse.getContent().isEmpty()) {
                            String msg = "Xin lỗi, chúng tôi chưa có sản phẩm này, bạn có thể tham khảo các sản phẩm khác tại đây: "
                                    + "http://localhost:3000/ProductGrid";
                            return new ChatResponse(msg, null);
                        } else {
                            String msg = "Chúng tôi có các sản phẩm " + keyword + " như sau, bạn có thể tham khảo:";
                            return new ChatResponse(msg, productResponse.getContent());
                        }
                    })
                    .onErrorResume(e -> {
                        System.err.println("Error during product search: " + e.getMessage());
                        String msg = "Xin lỗi, chúng tôi chưa có sản phẩm này, bạn có thể tham khảo các sản phẩm khác tại đây: "
                                + "http://localhost:3000/ProductGrid";
                        return Mono.just(new ChatResponse(msg, null));
                    });
        }

        // Ngược lại, đẩy sang AI để chat bình thường
        System.out.println("No shopping intent detected, calling AI API");
        String prompt = String.format(
                "Bạn là trợ lý ảo thân thiện của shop thời trang Alistyle. Câu hỏi: %s",
                sanitizeInput(userInput));
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequestBody(prompt))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseResponse)
                .map(msg -> new ChatResponse(msg, null))
                .onErrorResume(e -> Mono.just(
                        new ChatResponse("Xin lỗi, hiện tại không thể xử lý yêu cầu của bạn.", null)));
    }

    // Trích xuất tên sản phẩm từ input người dùng
    private List<String> extractProductNames(String userInput) {
        List<String> productNames = new ArrayList<>();

        Pattern pattern = Pattern.compile(
                "(?i)" +
                        "(?:(?:tôi|tui|tớ|tao|mình|em|anh|chị)\\s*)?" +
                        "(?:đang\\s*)?" +
                        "(?:" +
                        "muốn(?:\\s+(?:xem|mua|tìm|kiếm))?" +
                        "|cần(?:\\s+(?:xem|mua|tìm|kiếm))?" +
                        "|mua" +
                        "|tìm" +
                        "|kiếm" +
                        "|xem" +
                        ")" +
                        "\\s*([\\p{L}\\s]+)",
                Pattern.UNICODE_CHARACTER_CLASS);

        Matcher matcher = pattern.matcher(userInput);
        while (matcher.find()) {
            String name = matcher.group(1).trim()
                    .replaceAll("[\\p{Punct}\\s]+$", "");
            productNames.add(name);
            System.out.println("Extracted product name: " + name);
        }

        return productNames;
    }

    // Phân tích json phản hồi từ AI
    private Mono<String> parseResponse(String jsonResponse) {
        // System.out.println("Raw API response: " + jsonResponse); // Log phản hồi thô
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                // System.out.println("No candidates found in response"); // Log khi không có
                // phản hồi hợp lệ
                return Mono.just("Xin lỗi, tôi không thể trả lời câu hỏi này.");
            }

            JsonNode firstCandidate = candidates.get(0);
            String text = firstCandidate.path("content").path("parts").get(0).path("text").asText();
            // System.out.println("Parsed response text: " + text); // Log nội dung đã phân
            // tích
            return Mono.just(text);
        } catch (Exception e) {
            System.out.println("Error parsing response: " + e.getMessage());
            return Mono.just("Xin lỗi, có lỗi khi xử lý phản hồi.");
        }
    }

    // Tạo json body cho request đến AI api
    private String createRequestBody(String input) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            ArrayNode contents = objectMapper.createArrayNode();
            ObjectNode contentItem = objectMapper.createObjectNode();
            ArrayNode parts = objectMapper.createArrayNode();

            parts.add(objectMapper.createObjectNode().put("text", input));
            contentItem.set("parts", parts);
            contents.add(contentItem);

            requestBody.set("contents", contents);

            ObjectNode generationConfig = objectMapper.createObjectNode();
            generationConfig.put("temperature", 0.9);
            generationConfig.put("topP", 1);
            generationConfig.put("topK", 1);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.set("generationConfig", generationConfig);

            return objectMapper.writeValueAsString(requestBody);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request body", e);
        }
    }

    // Làm sạch input để tránh lỗi json
    private String sanitizeInput(String input) {
        return input.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}