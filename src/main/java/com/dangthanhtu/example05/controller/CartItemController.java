package com.dangthanhtu.example05.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dangthanhtu.example05.entity.CartItem;
import com.dangthanhtu.example05.payloads.CartItemDTO;
import com.dangthanhtu.example05.service.CartItemService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin(origins = "*")
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @GetMapping("/public/cartItem/{cartId}")
    public ResponseEntity<List<CartItemDTO>> getCartItemsByCartId(@PathVariable Long cartId) {
        List<CartItemDTO> cartItems = cartItemService.getCartItemsByCartId(cartId);
        return ResponseEntity.ok(cartItems);
    }

    @GetMapping("/public/cartItem/{cartId}/{productId}")
    public ResponseEntity<CartItemDTO> getCartItemByProductIdAndCartId(@PathVariable Long cartId, @PathVariable Long productId) {
        CartItemDTO cartItem = cartItemService.getCartItemByProductIdAndCartId(productId, cartId);
        return ResponseEntity.ok(cartItem);
    }
}