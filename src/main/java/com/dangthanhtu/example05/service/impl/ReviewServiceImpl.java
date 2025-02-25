package com.dangthanhtu.example05.service.impl;

import com.dangthanhtu.example05.entity.Review;
import com.dangthanhtu.example05.exceptions.ReviewNotFoundException;
import com.dangthanhtu.example05.payloads.ReviewDTO;
import com.dangthanhtu.example05.payloads.ReviewResponse;
import com.dangthanhtu.example05.repository.ReviewRepo;
import com.dangthanhtu.example05.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);
        return reviews.stream()
                      .map(review -> modelMapper.map(review, ReviewDTO.class))
                      .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse getReviewsByProductId(Long productId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Review> reviewsPage = reviewRepo.findByProductId(productId, pageable);

        List<ReviewDTO> content = reviewsPage.getContent()
                                             .stream()
                                             .map(review -> modelMapper.map(review, ReviewDTO.class))
                                             .collect(Collectors.toList());

        ReviewResponse reviewResponse = new ReviewResponse();
        reviewResponse.setContent(content);
        reviewResponse.setPageNumber(reviewsPage.getNumber());
        reviewResponse.setPageSize(reviewsPage.getSize());
        reviewResponse.setTotalElements(reviewsPage.getTotalElements());
        reviewResponse.setTotalPages(reviewsPage.getTotalPages());
        reviewResponse.setLastPage(reviewsPage.isLast());

        return reviewResponse;
    }

    @Override
    public ReviewDTO createReview(Long userId, Long productId, int rating, String comment, String userName) {
        Review review = new Review();
        review.setUserId(userId);
        review.setProductId(productId);
        review.setRating(rating);
        review.setComment(comment);
        review.setUserName(userName);

        Review savedReview = reviewRepo.save(review);
        return modelMapper.map(savedReview, ReviewDTO.class);
    }

    @Override
    public ReviewDTO updateReview(Long reviewId, int rating, String comment) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setRating(rating);
        review.setComment(comment);

        Review updatedReview = reviewRepo.save(review);
        return modelMapper.map(updatedReview, ReviewDTO.class);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        reviewRepo.delete(review);
    }
    @Override
    public ReviewDTO getReviewByUserIdAndProductId(Long userId, Long productId) {
        Review review = reviewRepo.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found for userId: " + userId + " and productId: " + productId));
        return modelMapper.map(review, ReviewDTO.class);
    }
}