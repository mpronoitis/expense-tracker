package com.app.expensetracker.controller;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.request.BudgetupdateRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.securityAnnotation.IdToCheck;
import com.app.expensetracker.securityAnnotation.SecurityLayer;
import com.app.expensetracker.service.BudgetService;
import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.Id;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@Validated
public class BudgetController {
    private final BudgetService budgetService;

    @Autowired
    public BudgetController(@Lazy BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    //get remaining budget for a category
    @Operation(summary = "Find remaining budget of a category", description = "Return the amount of budget")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/budget/{userId}/remaining/{budgetId}")
    public ApiResponse<BigDecimal> getRemaining(@IdToCheck @PathVariable("userId") Long userId, @RequestParam("categoryName") String categoryName, @PathVariable("budgetId") Long budgetId) {
        return new ApiResponse.Builder<BigDecimal>().payload(budgetService.getRemainingBudget(userId, categoryName, budgetId)).build();
    }

    //get all budgets for a user
    @Operation(summary = "Find all budgets of a user", description = "Return a list with the budgets")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/budgets/{userId}")
    public ApiResponse<List<BudgetResponseDTO>> findAll(@IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<List<BudgetResponseDTO>>().payload(budgetService.findAll(userId)).build();
    }

    @Operation(summary = "Create a new budget for a category", description = "Return the created Budget")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @PostMapping("/budgets/{userId}/create")
    public ApiResponse<BudgetResponseDTO> create(@IdToCheck @PathVariable("userId") Long userId, @Valid @RequestBody BudgetRequestDTO budgetRequestDTO) {
        return new ApiResponse.Builder<BudgetResponseDTO>().payload(budgetService.create(userId,budgetRequestDTO)).build();
    }

    @Operation(summary = "Update a Budget", description = "Return the updated Budget")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @PutMapping("/budgets/{userId}/update/{budgetId}")
    public ApiResponse<BudgetResponseDTO> update(@IdToCheck @PathVariable("userId") Long userId, @PathVariable("budgetId") Long budgetId,@Valid @RequestBody BudgetupdateRequestDTO budgetupdateRequestDTO) {
        return new ApiResponse.Builder<BudgetResponseDTO>().payload(budgetService.update(userId,budgetId,budgetupdateRequestDTO)).build();
    }

    @Operation(summary = "Delete a budget", description = "Return a message if the operation was successful")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @DeleteMapping("/budget/{userId}/delete/{budgetId}")
    public ApiResponse<String> delete(@IdToCheck @PathVariable("userId") Long userId, @PathVariable("budgetId") Long budgetId) {
        return new ApiResponse.Builder<String>().payload(budgetService.delete(userId, budgetId)).build();
    }


}
