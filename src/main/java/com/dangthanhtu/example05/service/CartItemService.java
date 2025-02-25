package com.dangthanhtu.example05.service;

import java.util.List;
import com.dangthanhtu.example05.entity.CartItem;
import com.dangthanhtu.example05.payloads.CartItemDTO;

public interface CartItemService {
    // List<CartItem> getCartItemsByCartId(Long cartId);
        List<CartItemDTO> getCartItemsByCartId(Long cartId);
        CartItemDTO getCartItemByProductIdAndCartId(Long productId, Long cartId);
}