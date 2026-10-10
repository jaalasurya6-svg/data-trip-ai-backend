package com.daytripai.controller;

import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.FavoriteResponse;
import com.daytripai.dto.response.FavoriteStatusResponse;
import com.daytripai.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@Tag(name = "User Favorites", description = "Endpoints for bookmarking and listing user favorite destinations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    @Operation(summary = "Get all favorite destinations bookmarked by the user")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> getFavorites(
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        List<FavoriteResponse> favorites = favoriteService.getUserFavorites(userId);
        return ResponseEntity.ok(ApiResponse.success("Favorites retrieved successfully", favorites));
    }

    @PostMapping("/{destinationId}")
    @Operation(summary = "Bookmark a destination as favorite")
    public ResponseEntity<ApiResponse<FavoriteResponse>> addFavorite(
            @PathVariable Long destinationId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        FavoriteResponse response = favoriteService.addFavorite(destinationId, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Destination added to favorites", response));
    }

    @DeleteMapping("/{destinationId}")
    @Operation(summary = "Remove a destination from favorites")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(
            @PathVariable Long destinationId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        favoriteService.removeFavorite(destinationId, userId);
        return ResponseEntity.ok(ApiResponse.success("Destination removed from favorites", null));
    }

    @GetMapping("/{destinationId}/status")
    @Operation(summary = "Check whether a destination is in the user's favorites")
    public ResponseEntity<ApiResponse<FavoriteStatusResponse>> checkFavoriteStatus(
            @PathVariable Long destinationId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        FavoriteStatusResponse response = favoriteService.checkFavoriteStatus(destinationId, userId);
        return ResponseEntity.ok(ApiResponse.success("Favorite status retrieved", response));
    }
}
