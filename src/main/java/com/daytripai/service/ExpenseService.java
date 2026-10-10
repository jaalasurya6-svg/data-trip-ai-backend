package com.daytripai.service;

import com.daytripai.dto.request.ExpenseRequest;
import com.daytripai.dto.response.ExpenseAnalyticsResponse;
import com.daytripai.dto.response.ExpenseResponse;

import java.util.List;

public interface ExpenseService {

    List<ExpenseResponse> getExpensesByTrip(Long tripId, Long authenticatedUserId);

    ExpenseResponse getExpenseById(Long tripId, Long expenseId, Long authenticatedUserId);

    ExpenseResponse createExpense(Long tripId, ExpenseRequest request, Long authenticatedUserId);

    ExpenseResponse updateExpense(Long tripId, Long expenseId, ExpenseRequest request, Long authenticatedUserId);

    void deleteExpense(Long tripId, Long expenseId, Long authenticatedUserId);

    ExpenseAnalyticsResponse getExpenseAnalytics(Long tripId, Long authenticatedUserId);
}
