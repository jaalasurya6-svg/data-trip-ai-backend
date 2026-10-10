package com.daytripai;

import com.daytripai.dto.request.ExpenseRequest;
import com.daytripai.enums.ExpenseCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "traveler@daytripai.com", authorities = {"ROLE_USER"})
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/trips/1/expenses returns logged trip expenses")
    void testGetExpenses() throws Exception {
        mockMvc.perform(get("/api/v1/trips/1/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", not(empty())));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/expenses logs new expense and updates spending")
    void testCreateExpense() throws Exception {
        ExpenseRequest request = new ExpenseRequest(
                "Afternoon Hydration & Coconut Water",
                150.0,
                ExpenseCategory.FOOD,
                LocalDateTime.now(),
                "Bought 3 tender coconuts near Golconda"
        );

        mockMvc.perform(post("/api/v1/trips/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Afternoon Hydration & Coconut Water")))
                .andExpect(jsonPath("$.data.amount", is(150.0)))
                .andExpect(jsonPath("$.data.category", is("FOOD")));
    }

    @Test
    @DisplayName("GET /api/v1/trips/1/expenses/analytics calculates category totals and budget utilization")
    void testGetExpenseAnalytics() throws Exception {
        mockMvc.perform(get("/api/v1/trips/1/expenses/analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.tripId", is(1)))
                .andExpect(jsonPath("$.data.totalBudget", is(2500.0)))
                .andExpect(jsonPath("$.data.actualTotalSpent", greaterThan(0.0)))
                .andExpect(jsonPath("$.data.categoryTotals", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/trips/1/expenses with negative amount fails validation")
    void testCreateExpenseValidationFailure() throws Exception {
        ExpenseRequest invalidRequest = new ExpenseRequest(
                "Invalid Expense",
                -50.0,
                ExpenseCategory.OTHER,
                LocalDateTime.now(),
                "Negative amount"
        );

        mockMvc.perform(post("/api/v1/trips/1/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}
