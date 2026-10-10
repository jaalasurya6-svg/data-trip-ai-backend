package com.daytripai.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO representing whether a destination is saved in the user's favorites.
 */
public class FavoriteStatusResponse {

    private Long destinationId;

    @JsonProperty("isFavorite")
    private boolean isFavorite;

    private Long favoriteId;

    public FavoriteStatusResponse() {
    }

    public FavoriteStatusResponse(Long destinationId, boolean isFavorite, Long favoriteId) {
        this.destinationId = destinationId;
        this.isFavorite = isFavorite;
        this.favoriteId = favoriteId;
    }

    // Getters and Setters
    public Long getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(Long destinationId) {
        this.destinationId = destinationId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Long getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(Long favoriteId) {
        this.favoriteId = favoriteId;
    }
}
