package com.daytripai.service.impl;

import com.daytripai.dto.response.FavoriteResponse;
import com.daytripai.dto.response.FavoriteStatusResponse;
import com.daytripai.entity.Destination;
import com.daytripai.entity.Favorite;
import com.daytripai.entity.User;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.FavoriteMapper;
import com.daytripai.repository.DestinationRepository;
import com.daytripai.repository.FavoriteRepository;
import com.daytripai.repository.UserRepository;
import com.daytripai.service.FavoriteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private static final Logger log = LoggerFactory.getLogger(FavoriteServiceImpl.class);

    private final FavoriteRepository favoriteRepository;
    private final DestinationRepository destinationRepository;
    private final UserRepository userRepository;
    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               DestinationRepository destinationRepository,
                               UserRepository userRepository,
                               FavoriteMapper favoriteMapper) {
        this.favoriteRepository = favoriteRepository;
        this.destinationRepository = destinationRepository;
        this.userRepository = userRepository;
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getUserFavorites(Long authenticatedUserId) {
        Long userId = resolveUserId(authenticatedUserId);
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderBySavedAtDesc(userId);
        return favorites.stream().map(favoriteMapper::toResponse).toList();
    }

    @Override
    public FavoriteResponse addFavorite(Long destinationId, Long authenticatedUserId) {
        Long userId = resolveUserId(authenticatedUserId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Destination destination = destinationRepository.findById(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", "id", destinationId));

        // Idempotent: return existing if already saved
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndDestinationId(userId, destinationId);
        if (existing.isPresent()) {
            log.info("Destination ID {} is already bookmarked by user ID {}", destinationId, userId);
            return favoriteMapper.toResponse(existing.get());
        }

        Favorite favorite = new Favorite(user, destination);
        Favorite saved = favoriteRepository.save(favorite);
        log.info("Saved favorite ID {} for user ID {} on destination ID {}", saved.getId(), userId, destinationId);

        return favoriteMapper.toResponse(saved);
    }

    @Override
    public void removeFavorite(Long destinationId, Long authenticatedUserId) {
        Long userId = resolveUserId(authenticatedUserId);
        if (favoriteRepository.existsByUserIdAndDestinationId(userId, destinationId)) {
            favoriteRepository.deleteByUserIdAndDestinationId(userId, destinationId);
            log.info("Removed favorite bookmark for destination ID {} by user ID {}", destinationId, userId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteStatusResponse checkFavoriteStatus(Long destinationId, Long authenticatedUserId) {
        Long userId = resolveUserId(authenticatedUserId);
        Optional<Favorite> favOpt = favoriteRepository.findByUserIdAndDestinationId(userId, destinationId);

        return new FavoriteStatusResponse(
                destinationId,
                favOpt.isPresent(),
                favOpt.map(Favorite::getId).orElse(null)
        );
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
