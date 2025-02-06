package com.app.expensetracker.controller;
import com.app.expensetracker.dto.request.IncomeRequestDTO;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import com.app.expensetracker.dto.response.TotalIncomeResponseDTO;
import com.app.expensetracker.service.IncomeService;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/incomes/total")
    public ApiResponse<TotalIncomeResponseDTO> getTotalIncome() {
        return new ApiResponse.Builder<TotalIncomeResponseDTO>().payload(incomeService.getTotalIncome()).build();
    }

}
