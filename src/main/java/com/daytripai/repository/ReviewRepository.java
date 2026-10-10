package com.daytripai.repository;

import com.daytripai.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByDestinationIdOrderByCreatedAtDesc(Long destinationId);

    Page<Review> findByDestinationIdOrderByCreatedAtDesc(Long destinationId, Pageable pageable);

    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.destination.id = :destinationId")
    Double calculateAverageRatingByDestinationId(@Param("destinationId") Long destinationId);

    long countByDestinationId(Long destinationId);
}
