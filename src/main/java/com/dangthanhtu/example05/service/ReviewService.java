package com.dangthanhtu.example05.service;

import com.dangthanhtu.example05.payloads.ReviewDTO;
import com.dangthanhtu.example05.payloads.ReviewResponse;
import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getReviewsByProductId(Long productId);
    ReviewResponse getReviewsByProductId(Long productId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    ReviewDTO createReview(Long userId, Long productId, int rating, String comment, String userName);
    ReviewDTO updateReview(Long reviewId, int rating, String comment);
    void deleteReview(Long reviewId);
    ReviewDTO getReviewByUserIdAndProductId(Long userId, Long productId);
}
