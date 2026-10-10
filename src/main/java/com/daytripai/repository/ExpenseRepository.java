package com.daytripai.repository;

import com.daytripai.entity.Expense;
import com.daytripai.enums.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByTripIdOrderBySpentAtDesc(Long tripId);

    List<Expense> findByTripIdAndCategory(Long tripId, ExpenseCategory category);

    Optional<Expense> findByIdAndTripId(Long id, Long tripId);

    @Query("SELECT COALESCE(SUM(e.amount), 0.0) FROM Expense e WHERE e.trip.id = :tripId")
    Double calculateTotalSpentByTripId(@Param("tripId") Long tripId);

    @Query("SELECT e.category, SUM(e.amount) FROM Expense e WHERE e.trip.id = :tripId GROUP BY e.category")
    List<Object[]> getCategoryWiseBreakdownByTripId(@Param("tripId") Long tripId);

    void deleteByTripId(Long tripId);
}
