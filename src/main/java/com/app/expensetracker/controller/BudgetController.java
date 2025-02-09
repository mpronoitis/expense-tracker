package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.securityAnnotation.IdToCheck;
import com.app.expensetracker.securityAnnotation.SecurityLayer;
import com.app.expensetracker.service.BudgetService;
import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;
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
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/budget/{userId}/remaining/{budgetId}")
    public ApiResponse<BigDecimal> getRemaining(@IdToCheck @PathVariable("userId") Long userId, @RequestParam("categoryName") String categoryName, @PathVariable("budgetId") Long budgetId) {
        return new ApiResponse.Builder<BigDecimal>().payload(budgetService.getRemainingBudget(userId, categoryName, budgetId)).build();
    }

    //get all budgets for a user
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/budgets/{userId}")
    public ApiResponse<List<BudgetResponseDTO>> findAll(@IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<List<BudgetResponseDTO>>().payload(budgetService.findAll(userId)).build();
    }

    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @PostMapping("/budgets/{userId}/create")
    private ApiResponse<BudgetResponseDTO> create(@IdToCheck @PathVariable("userId") Long userId, @Valid @RequestBody BudgetRequestDTO budgetRequestDTO) {
        return new ApiResponse.Builder<BudgetResponseDTO>().payload(budgetService.create(userId,budgetRequestDTO)).build();
    }
}
