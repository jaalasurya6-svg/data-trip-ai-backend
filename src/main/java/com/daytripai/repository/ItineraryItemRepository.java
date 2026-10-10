package com.daytripai.repository;

import com.daytripai.entity.ItineraryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryItemRepository extends JpaRepository<ItineraryItem, Long> {

    List<ItineraryItem> findByItineraryIdOrderByStopOrderAsc(Long itineraryId);

    void deleteByItineraryId(Long itineraryId);
}
