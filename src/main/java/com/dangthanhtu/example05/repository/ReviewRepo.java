package com.dangthanhtu.example05.repository;

import com.dangthanhtu.example05.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    // Tìm review theo productId (không phân trang)
    List<Review> findByProductId(Long productId);

    // Tìm review theo productId với phân trang
    Page<Review> findByProductId(Long productId, Pageable pageable);

    
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

}
