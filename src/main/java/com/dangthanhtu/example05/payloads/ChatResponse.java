package com.dangthanhtu.example05.payloads;

import java.util.List;

public class ChatResponse {
    private String message;
    private List<ProductDTO> products;

    public ChatResponse(String message, List<ProductDTO> products) {
        this.message = message;
        this.products = products;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<ProductDTO> getProducts() { return products; }
    public void setProducts(List<ProductDTO> products) { this.products = products; }
}
