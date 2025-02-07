package com.app.expensetracker.controller;

import com.app.expensetracker.dto.request.ExpenseRequestDTO;
import com.app.expensetracker.dto.response.ExpenseResponseDTO;
import com.app.expensetracker.service.ExpenseService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @GetMapping("/expenses/{userId}")
    public ApiResponse<List<ExpenseResponseDTO>> getAllExpenses(@PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<List<ExpenseResponseDTO>>().payload(expenseService.getAll(userId)).build();
    }

    @PostMapping("/expenses/{userId}/create")
    public ApiResponse<ExpenseResponseDTO> createExpense(@Valid @RequestBody ExpenseRequestDTO expenseRequestDTO, @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<ExpenseResponseDTO>().payload(expenseService.create(userId,expenseRequestDTO)).build();
    }
}
