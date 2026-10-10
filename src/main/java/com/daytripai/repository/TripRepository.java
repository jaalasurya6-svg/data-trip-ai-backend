package com.daytripai.repository;

import com.daytripai.entity.Trip;
import com.daytripai.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByUserIdOrderByCreatedAtDesc(Long userId);

    Page<Trip> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    Page<Trip> findByUserIdAndStatusOrderByTripDateAsc(Long userId, TripStatus status, Pageable pageable);

    Optional<Trip> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);
}
