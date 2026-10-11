package com.daytripai.controller;

import com.daytripai.dto.request.ExpenseRequest;
import com.daytripai.dto.response.ApiResponse;
import com.daytripai.dto.response.ExpenseAnalyticsResponse;
import com.daytripai.dto.response.ExpenseResponse;
import com.daytripai.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trips/{tripId}/expenses")
@Tag(name = "Trip Expenses", description = "Endpoints for tracking, logging, and analyzing trip expenses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @Operation(summary = "Get all logged expenses for a trip")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(
            @PathVariable Long tripId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        List<ExpenseResponse> expenses = expenseService.getExpensesByTrip(tripId, userId);
        return ResponseEntity.ok(ApiResponse.success("Expenses retrieved successfully", expenses));
    }

    @GetMapping("/{expenseId:[0-9]+}")
    @Operation(summary = "Get an individual expense by ID")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpenseById(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ExpenseResponse response = expenseService.getExpenseById(tripId, expenseId, userId);
        return ResponseEntity.ok(ApiResponse.success("Expense retrieved successfully", response));
    }

    @PostMapping
    @Operation(summary = "Log a new expense against a trip and update total spent")
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody ExpenseRequest request,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ExpenseResponse response = expenseService.createExpense(tripId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Expense logged successfully", response));
    }

    @PutMapping("/{expenseId:[0-9]+}")
    @Operation(summary = "Update an existing expense")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @Valid @RequestBody ExpenseRequest request,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ExpenseResponse response = expenseService.updateExpense(tripId, expenseId, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    @DeleteMapping("/{expenseId:[0-9]+}")
    @Operation(summary = "Delete an expense and recalculate trip spending")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(
            @PathVariable Long tripId,
            @PathVariable Long expenseId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        expenseService.deleteExpense(tripId, expenseId, userId);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get category breakdown and budget utilization analytics for a trip")
    public ResponseEntity<ApiResponse<ExpenseAnalyticsResponse>> getExpenseAnalytics(
            @PathVariable Long tripId,
            @RequestAttribute(value = "userId", required = false) Long authenticatedUserId) {

        Long userId = com.daytripai.security.SecurityUtils.getCurrentUserId().orElse(authenticatedUserId);
        ExpenseAnalyticsResponse analytics = expenseService.getExpenseAnalytics(tripId, userId);
        return ResponseEntity.ok(ApiResponse.success("Expense analytics computed successfully", analytics));
    }
}
