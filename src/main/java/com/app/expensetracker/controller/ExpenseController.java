package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.ExpenseRequestDTO;
import com.app.expensetracker.dto.request.ExpenseUpdateRequestDTO;
import com.app.expensetracker.dto.response.ExpenseResponseDTO;
import com.app.expensetracker.securityAnnotation.IdToCheck;
import com.app.expensetracker.securityAnnotation.SecurityLayer;
import com.app.expensetracker.service.ExpenseService;
import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @Operation(summary = "Get all expenses of the user", description = "Return a list of all expenses")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/expenses/{userId}")
    public ApiResponse<List<ExpenseResponseDTO>> getAllExpenses(@IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<List<ExpenseResponseDTO>>().payload(expenseService.getAll(userId)).build();
    }

    @Operation(summary = "Create a new expense for a specific category", description = "Return the created Expense")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @PostMapping("/expenses/{userId}/create")
    public ApiResponse<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO expenseRequestDTO, @IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<ExpenseResponseDTO>().payload(expenseService.create(userId,expenseRequestDTO)).build();
    }

    @Operation(summary = "Update an expense", description = "Return the updated Expense")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @PutMapping("/expenses/{userId}/update/{expenseId}")
    public ApiResponse<ExpenseResponseDTO> update(@RequestBody ExpenseUpdateRequestDTO updateRequestDTO,@IdToCheck @PathVariable("userId") Long userId,@PathVariable("expenseId") Long expenseId) {
        return new ApiResponse.Builder<ExpenseResponseDTO>().payload(expenseService.update(updateRequestDTO, userId, expenseId)).build();
    }
}
