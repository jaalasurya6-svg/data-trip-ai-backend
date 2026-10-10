package com.daytripai.mapper;

import com.daytripai.dto.request.ExpenseRequest;
import com.daytripai.dto.response.ExpenseResponse;
import com.daytripai.entity.Expense;
import com.daytripai.entity.Trip;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper for converting between Expense entities and DTOs.
 */
@Component
public class ExpenseMapper {

    public ExpenseResponse toResponse(Expense expense) {
        if (expense == null) {
            return null;
        }
        return new ExpenseResponse(
                expense.getId(),
                expense.getTrip() != null ? expense.getTrip().getId() : null,
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getSpentAt(),
                expense.getNotes(),
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }

    public Expense toEntity(ExpenseRequest request, Trip trip) {
        if (request == null) {
            return null;
        }
        return new Expense(
                trip,
                request.getTitle(),
                request.getAmount(),
                request.getCategory(),
                request.getSpentAt() != null ? request.getSpentAt() : LocalDateTime.now(),
                request.getNotes()
        );
    }

    public void updateEntity(Expense expense, ExpenseRequest request) {
        if (expense == null || request == null) {
            return;
        }
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        if (request.getSpentAt() != null) {
            expense.setSpentAt(request.getSpentAt());
        }
        expense.setNotes(request.getNotes());
    }
}
