package com.daytripai.mapper;

import com.daytripai.dto.request.ReviewRequest;
import com.daytripai.dto.response.ReviewResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Review;
import com.daytripai.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting Review entity to DTOs.
 */
@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }
        Destination destination = review.getDestination();
        User author = review.getUser();

        return new ReviewResponse(
                review.getId(),
                destination != null ? destination.getId() : null,
                destination != null ? destination.getName() : null,
                author != null ? author.getId() : null,
                author != null ? author.getFullName() : "Anonymous Traveler",
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public Review toEntity(ReviewRequest request, User user, Destination destination) {
        if (request == null) {
            return null;
        }
        return new Review(
                user,
                destination,
                request.getRating(),
                request.getComment()
        );
    }
}
