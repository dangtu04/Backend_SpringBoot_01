package com.dangthanhtu.example05.controller;

import com.dangthanhtu.example05.exceptions.ReviewNotFoundException;
import com.dangthanhtu.example05.payloads.ReviewDTO;
import com.dangthanhtu.example05.payloads.ReviewResponse;
import com.dangthanhtu.example05.service.ReviewService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/reviews")
@SecurityRequirement(name = "E-Commerce Application")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    @GetMapping("/product/{productId}/paged")
    public ResponseEntity<ReviewResponse> getReviewsByProductIdPaged(
            @PathVariable Long productId,
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder) {
        ReviewResponse reviewResponse = reviewService.getReviewsByProductId(productId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(reviewResponse, HttpStatus.OK);
    }

    @PostMapping("/userId/{userId}/productId/{productId}")
    public ResponseEntity<ReviewDTO> createReview(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestBody ReviewDTO reviewDTO) {
        reviewDTO.setUserId(userId);
        reviewDTO.setProductId(productId);
        ReviewDTO createdReview = reviewService.createReview(userId, productId, reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserName());
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestParam int rating,
            @RequestBody(required = false) String comment) {
        ReviewDTO reviewDTO = reviewService.updateReview(reviewId, rating, comment);
        return new ResponseEntity<>(reviewDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/user/{userId}/product/{productId}")
    public ResponseEntity<ReviewDTO> getReviewByUserIdAndProductId(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        try {
            ReviewDTO review = reviewService.getReviewByUserIdAndProductId(userId, productId);
            return new ResponseEntity<>(review, HttpStatus.OK);
        } catch (ReviewNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}