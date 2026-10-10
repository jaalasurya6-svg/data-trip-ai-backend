package com.daytripai.mapper;

import com.daytripai.dto.response.FavoriteResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Favorite;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting Favorite entity to FavoriteResponse DTO.
 */
@Component
public class FavoriteMapper {

    public FavoriteResponse toResponse(Favorite favorite) {
        if (favorite == null) {
            return null;
        }
        Destination dest = favorite.getDestination();
        return new FavoriteResponse(
                favorite.getId(),
                dest != null ? dest.getId() : null,
                dest != null ? dest.getName() : null,
                dest != null ? dest.getCity() : null,
                dest != null ? dest.getState() : null,
                dest != null ? dest.getCategory() : null,
                dest != null ? dest.getEstimatedCost() : null,
                dest != null ? dest.getRating() : null,
                dest != null ? dest.getImageUrl() : null,
                favorite.getSavedAt()
        );
    }
}
