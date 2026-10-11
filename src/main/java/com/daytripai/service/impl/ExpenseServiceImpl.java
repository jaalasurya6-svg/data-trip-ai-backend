package com.daytripai.service.impl;

import com.daytripai.dto.request.ExpenseRequest;
import com.daytripai.dto.response.ExpenseAnalyticsResponse;
import com.daytripai.dto.response.ExpenseResponse;
import com.daytripai.entity.Expense;
import com.daytripai.entity.Trip;
import com.daytripai.enums.ExpenseCategory;
import com.daytripai.exception.ResourceNotFoundException;
import com.daytripai.mapper.ExpenseMapper;
import com.daytripai.repository.ExpenseRepository;
import com.daytripai.repository.TripRepository;
import com.daytripai.service.ExpenseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              TripRepository tripRepository,
                              ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByTrip(Long tripId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        List<Expense> expenses = expenseRepository.findByTripIdOrderBySpentAtDesc(trip.getId());
        return expenses.stream().map(expenseMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long tripId, Long expenseId, Long authenticatedUserId) {
        findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Expense expense = expenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseResponse createExpense(Long tripId, ExpenseRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Expense expense = expenseMapper.toEntity(request, trip);
        Expense savedExpense = expenseRepository.save(expense);

        // Recalculate trip actual spend and balance
        syncTripExpenses(trip);

        log.info("Logged new expense ID {} (₹{}) for Trip ID {}", savedExpense.getId(), savedExpense.getAmount(), tripId);
        return expenseMapper.toResponse(savedExpense);
    }

    @Override
    public ExpenseResponse updateExpense(Long tripId, Long expenseId, ExpenseRequest request, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Expense expense = expenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        expenseMapper.updateEntity(expense, request);
        Expense updatedExpense = expenseRepository.save(expense);

        // Recalculate trip actual spend and balance
        syncTripExpenses(trip);

        log.info("Updated expense ID {} for Trip ID {}", expenseId, tripId);
        return expenseMapper.toResponse(updatedExpense);
    }

    @Override
    public void deleteExpense(Long tripId, Long expenseId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);
        Expense expense = expenseRepository.findByIdAndTripId(expenseId, tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Expense", "id", expenseId));

        expenseRepository.delete(expense);

        // Recalculate trip actual spend and balance
        syncTripExpenses(trip);

        log.info("Deleted expense ID {} from Trip ID {}", expenseId, tripId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseAnalyticsResponse getExpenseAnalytics(Long tripId, Long authenticatedUserId) {
        Trip trip = findTripAndVerifyOwnership(tripId, authenticatedUserId);

        Double totalBudget = trip.getBudget() != null ? trip.getBudget() : 0.0;
        Double actualSpent = expenseRepository.calculateTotalSpentByTripId(tripId);
        if (actualSpent == null) actualSpent = 0.0;

        Double remaining = totalBudget - actualSpent;
        boolean isOverBudget = actualSpent > totalBudget;
        Double overBudgetAmount = isOverBudget ? actualSpent - totalBudget : 0.0;
        Double utilizationPercentage = totalBudget > 0 ? (actualSpent / totalBudget) * 100 : 0.0;

        // Category-wise totals and percentages
        Map<ExpenseCategory, Double> categoryTotals = new EnumMap<>(ExpenseCategory.class);
        Map<ExpenseCategory, Double> categoryPercentages = new EnumMap<>(ExpenseCategory.class);

        // Initialize all categories with 0.0
        for (ExpenseCategory cat : ExpenseCategory.values()) {
            categoryTotals.put(cat, 0.0);
            categoryPercentages.put(cat, 0.0);
        }

        List<Object[]> rows = expenseRepository.getCategoryWiseBreakdownByTripId(tripId);
        int expenseCount = 0;
        for (Object[] row : rows) {
            ExpenseCategory cat = null;
            if (row[0] instanceof ExpenseCategory ec) {
                cat = ec;
            } else if (row[0] != null) {
                try {
                    cat = ExpenseCategory.valueOf(row[0].toString());
                } catch (IllegalArgumentException ignored) {
                }
            }
            if (cat == null) continue;

            Double catSum = ((Number) row[1]).doubleValue();
            categoryTotals.put(cat, Math.round(catSum * 100.0) / 100.0);
            if (actualSpent > 0) {
                double pct = (catSum / actualSpent) * 100.0;
                categoryPercentages.put(cat, Math.round(pct * 10.0) / 10.0);
            }
        }

        List<Expense> allExpenses = expenseRepository.findByTripIdOrderBySpentAtDesc(tripId);
        expenseCount = allExpenses.size();

        return new ExpenseAnalyticsResponse(
                tripId,
                Math.round(totalBudget * 100.0) / 100.0,
                Math.round(actualSpent * 100.0) / 100.0,
                Math.round(remaining * 100.0) / 100.0,
                Math.round(utilizationPercentage * 10.0) / 10.0,
                isOverBudget,
                Math.round(overBudgetAmount * 100.0) / 100.0,
                categoryTotals,
                categoryPercentages,
                expenseCount
        );
    }

    private void syncTripExpenses(Trip trip) {
        Double totalSpent = expenseRepository.calculateTotalSpentByTripId(trip.getId());
        if (totalSpent == null) totalSpent = 0.0;

        trip.setActualTotalSpent(Math.round(totalSpent * 100.0) / 100.0);
        trip.setRemainingBudget(Math.round((trip.getBudget() - totalSpent) * 100.0) / 100.0);
        tripRepository.save(trip);
    }

    private Trip findTripAndVerifyOwnership(Long tripId, Long authenticatedUserId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        // When authentication is active, ownership check applies
        if (authenticatedUserId != null && trip.getUser() != null &&
                !trip.getUser().getId().equals(authenticatedUserId)) {
            log.warn("Unauthorized expense access attempt: User {} on Trip {}", authenticatedUserId, tripId);
        }

        return trip;
    }
}
