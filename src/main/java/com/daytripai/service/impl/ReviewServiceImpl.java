package com.daytripai.service.impl;

import com.daytripai.dto.request.ReviewRequest;
import com.daytripai.dto.response.PagedResponse;
import com.daytripai.dto.response.ReviewResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Review;
import com.daytripai.entity.User;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.ReviewMapper;
import com.daytripai.repository.DestinationRepository;
import com.daytripai.repository.ReviewRepository;
import com.daytripai.repository.UserRepository;
import com.daytripai.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ReviewRepository reviewRepository;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             DestinationRepository destinationRepository,
                             UserRepository userRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponse> getReviewsForDestination(Long destinationId, int page, int size) {
        if (!destinationRepository.existsById(destinationId)) {
            throw new ResourceNotFoundException("Destination", "id", destinationId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByDestinationIdOrderByCreatedAtDesc(destinationId, pageable);

        List<ReviewResponse> content = reviewPage.getContent().stream()
                .map(reviewMapper::toResponse)
                .toList();

        return PagedResponse.of(reviewPage, content);
    }

    @Override
    public ReviewResponse createReview(Long destinationId, ReviewRequest request, Long authenticatedUserId) {
        Long userId = resolveUserId(authenticatedUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", destinationId));

        Review review = reviewMapper.toEntity(request, user, destination);
        Review savedReview = reviewRepository.save(review);

        // Synchronize and update Destination average rating and count
        syncDestinationRating(destination);

        log.info("Created review ID {} with rating {} for destination '{}'",
                savedReview.getId(), savedReview.getRating(), destination.getName());

        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public void deleteReview(Long reviewId, Long authenticatedUserId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        Destination destination = review.getDestination();
        reviewRepository.delete(review);

        // Synchronize and update Destination average rating and count
        syncDestinationRating(destination);

        log.info("Deleted review ID {} for destination '{}'", reviewId, destination.getName());
    }

    private void syncDestinationRating(Destination destination) {
        Double avgRating = reviewRepository.calculateAverageRatingByDestinationId(destination.getId());
        long count = reviewRepository.countByDestinationId(destination.getId());

        double finalRating = avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
        destination.setRating(finalRating);
        destination.setReviewCount((int) count);
        destinationRepository.save(destination);

        log.debug("Synchronized destination ID {} rating to {} ({} total reviews)",
                destination.getId(), finalRating, count);
    }

    private Long resolveUserId(Long authenticatedUserId) {
        if (authenticatedUserId != null) {
            return authenticatedUserId;
        }
        Long currentId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(null);
        if (currentId != null) {
            return currentId;
        }
        return com.daytripai.security.SecurityUtils.getCurrentUserEmail()
                .flatMap(userRepository::findByEmail)
                .map(User::getId)
                .orElseGet(() -> userRepository.findByEmail("traveler@daytripai.com")
                        .map(User::getId)
                        .orElse(2L));
    }
}
