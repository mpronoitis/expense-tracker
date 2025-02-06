package com.app.expensetracker.controller;
import com.app.expensetracker.dto.request.IncomeRequestDTO;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import com.app.expensetracker.dto.response.TotalIncomeResponseDTO;
import com.app.expensetracker.service.IncomeService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @GetMapping("/incomes")
    public ApiResponse<List<IncomeResponseDTO>> getIncomes() {
        return new ApiResponse.Builder<List<IncomeResponseDTO>>().payload(incomeService.getIncomes()).build();
    }

    @PostMapping("/incomes")
    public ApiResponse<IncomeResponseDTO> createIncome(@Valid @RequestBody IncomeRequestDTO incomeRequestDTO) {
        return new ApiResponse.Builder<IncomeResponseDTO>().payload(incomeService.create(incomeRequestDTO)).build();
    }

    //Get total-income of user
    @GetMapping("/incomes/{userId}/total")
    public ApiResponse<TotalIncomeResponseDTO> getTotalIncome(@PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<TotalIncomeResponseDTO>().payload(incomeService.getTotalIncome(userId)).build();
    }

    //Get net income of a user. Net Income = Total Income - Total Expenses
    @GetMapping("/incomes/{userId}/net-income")
    public ApiResponse<TotalIncomeResponseDTO> getNetIncome(@PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<TotalIncomeResponseDTO>().payload(incomeService.getNetIncome(userId)).build();
    }

}
