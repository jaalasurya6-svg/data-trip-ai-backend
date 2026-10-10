package com.daytripai.service;

import com.daytripai.dto.response.FavoriteResponse;
import com.daytripai.dto.response.FavoriteStatusResponse;

import java.util.List;

public interface FavoriteService {

    List<FavoriteResponse> getUserFavorites(Long authenticatedUserId);

    FavoriteResponse addFavorite(Long destinationId, Long authenticatedUserId);

    void removeFavorite(Long destinationId, Long authenticatedUserId);

    FavoriteStatusResponse checkFavoriteStatus(Long destinationId, Long authenticatedUserId);
}
