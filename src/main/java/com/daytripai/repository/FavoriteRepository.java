package com.daytripai.repository;

import com.daytripai.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderBySavedAtDesc(Long userId);

    Page<Favorite> findByUserIdOrderBySavedAtDesc(Long userId, Pageable pageable);

    Optional<Favorite> findByUserIdAndDestinationId(Long userId, Long destinationId);

    boolean existsByUserIdAndDestinationId(Long userId, Long destinationId);

    void deleteByUserIdAndDestinationId(Long userId, Long destinationId);

    long countByUserId(Long userId);
}
