package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.service.BudgetService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    //get remaining budget for a category
    @GetMapping("/budget/{userId}/remaining/{budgetId}")
    public ApiResponse<BigDecimal> getRemaining(@PathVariable("userId") Long userId, @RequestParam("categoryName") String categoryName, @PathVariable("budgetId") Long budgetId) {
        return new ApiResponse.Builder<BigDecimal>().payload(budgetService.getRemainingBudget(userId, categoryName, budgetId)).build();
    }

    //get all budgets for a user
    @GetMapping("/budgets/{userId}")
    public ApiResponse<List<BudgetResponseDTO>> findAll(@PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<List<BudgetResponseDTO>>().payload(budgetService.findAll(userId)).build();
    }

    @PostMapping("/budgets/{userId}/create")
    private ApiResponse<BudgetResponseDTO> create(@PathVariable("userId") Long userId, @Valid @RequestBody BudgetRequestDTO budgetRequestDTO) {
        return new ApiResponse.Builder<BudgetResponseDTO>().payload(budgetService.create(userId,budgetRequestDTO)).build();
    }
}
