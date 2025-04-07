package com.dangthanhtu.example05.controller;

import org.springframework.web.bind.annotation.*;

import com.dangthanhtu.example05.payloads.ChatResponse;
import com.dangthanhtu.example05.service.ChatbotService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public Mono<ChatResponse> chat(@RequestBody String userInput) {
        return chatbotService.getAiResponse(userInput)
                .onErrorReturn(new ChatResponse("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn", null));
    }
}