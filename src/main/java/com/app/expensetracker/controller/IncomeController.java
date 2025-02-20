package com.app.expensetracker.controller;
import com.app.expensetracker.dto.request.IncomeRequestDTO;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import com.app.expensetracker.dto.response.TotalIncomeResponseDTO;
import com.app.expensetracker.securityAnnotation.IdToCheck;
import com.app.expensetracker.securityAnnotation.SecurityLayer;
import com.app.expensetracker.service.IncomeService;
import com.app.expensetracker.shared.rest.enumeration.SecurityQueryEnum;
import com.app.expensetracker.shared.rest.model.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @Operation(summary = "Find all incomes of a user", description = "Return a list with all incomes")
    @GetMapping("/incomes")
    public ApiResponse<List<IncomeResponseDTO>> getIncomes() {
        return new ApiResponse.Builder<List<IncomeResponseDTO>>().payload(incomeService.getIncomes()).build();
    }

    @Operation(summary = "Create a new Income for a user", description = "Return the created Income")
    @PostMapping("/incomes")
    public ApiResponse<IncomeResponseDTO> createIncome(@Valid @RequestBody IncomeRequestDTO incomeRequestDTO) {
        return new ApiResponse.Builder<IncomeResponseDTO>().payload(incomeService.create(incomeRequestDTO)).build();
    }

    @Operation(summary = "Get the total Income of a user", description = "Return the total income")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/incomes/{userId}/total")
    public ApiResponse<TotalIncomeResponseDTO> getTotalIncome(@IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<TotalIncomeResponseDTO>().payload(incomeService.getTotalIncome(userId)).build();
    }

    @Operation(summary = "Get the Net Income of a User(Total Income - Expenses)", description = "Return the Net Income")
    @SecurityLayer(securityQueryEnum = SecurityQueryEnum.RETRIEVE_USER)
    @GetMapping("/incomes/{userId}/net-income")
    public ApiResponse<TotalIncomeResponseDTO> getNetIncome(@IdToCheck @PathVariable("userId") Long userId) {
        return new ApiResponse.Builder<TotalIncomeResponseDTO>().payload(incomeService.getNetIncome(userId)).build();
    }

}
