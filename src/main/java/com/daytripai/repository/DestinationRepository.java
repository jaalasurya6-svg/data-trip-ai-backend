package com.daytripai.repository;

import com.daytripai.entity.Destination;
import com.daytripai.enums.DestinationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    boolean existsByName(String name);

    List<Destination> findByCityIgnoreCase(String city);

    Page<Destination> findByCityIgnoreCase(String city, Pageable pageable);

    Page<Destination> findByCategory(DestinationCategory category, Pageable pageable);

    Page<Destination> findByCityIgnoreCaseAndCategory(String city, DestinationCategory category, Pageable pageable);

    List<Destination> findByIsPopularTrue();

    Page<Destination> findByIsPopularTrue(Pageable pageable);

    @Query("SELECT d FROM Destination d WHERE " +
            "(:keyword IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:city IS NULL OR LOWER(d.city) = LOWER(:city)) AND " +
            "(:category IS NULL OR d.category = :category) AND " +
            "(:maxCost IS NULL OR d.estimatedCost <= :maxCost)")
    Page<Destination> searchDestinations(
            @Param("keyword") String keyword,
            @Param("city") String city,
            @Param("category") DestinationCategory category,
            @Param("maxCost") Double maxCost,
            Pageable pageable);

    @Query("SELECT DISTINCT d.city FROM Destination d ORDER BY d.city ASC")
    List<String> findDistinctCities();
}
