package com.app.expensetracker.service;

import com.app.expensetracker.domain.Income;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.dto.request.IncomeRequestDTO;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import com.app.expensetracker.dto.response.TotalIncomeResponseDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.mapper.IncomeMapper;
import com.app.expensetracker.repository.ExpenseRepository;
import com.app.expensetracker.repository.IncomeRepository;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final IncomeMapper incomeMapper;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    public List<IncomeResponseDTO> getIncomes() {

        //find the AuthenticatedUser
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserClaimsDTO userClaimsDTO = (UserClaimsDTO) authentication.getPrincipal();

        List<Income> incomesByUser = incomeRepository.findIncomesByUserId(userClaimsDTO.getId());

        return incomeMapper.toDto(incomesByUser);
    }

    @Transactional(rollbackOn = Exception.class)
    public IncomeResponseDTO create(IncomeRequestDTO incomeRequestDTO) {
        UserClaimsDTO userClaimsDTO = UserClaimsService.getUserClaimsDTO();
        User user = userRepository.findById(userClaimsDTO.getId()).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));

        //Create Income
        Income income = new Income();
        income.setDate(incomeRequestDTO.getDate());
        income.setSource(incomeRequestDTO.getSource());
        income.setUser(user);
        income.setAmount(incomeRequestDTO.getAmount());
        income.setPaymentMethod(incomeRequestDTO.getPaymentMethod());

        Income savedIncome = incomeRepository.save(income);

        return incomeMapper.toDto(savedIncome);
    }

    public TotalIncomeResponseDTO getTotalIncome(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        Set<Income> incomes =  user.getIncomes();
        BigDecimal totalIncome = BigDecimal.ZERO;

        for (Income income: incomes) {
            totalIncome = totalIncome.add(income.getAmount());
        }

        TotalIncomeResponseDTO totalIncomeResponseDTO = new TotalIncomeResponseDTO();
        totalIncomeResponseDTO.setTotalIncome(totalIncome);

        return totalIncomeResponseDTO;
    }

    public TotalIncomeResponseDTO getNetIncome(Long userId) {
        //find all Incomes of the user
        BigDecimal totalIncome = incomeRepository.getTotalIncomesByUserId(userId);
        BigDecimal totalExpenses = expenseRepository.getTotalAmountExpensesByUserId(userId);
       TotalIncomeResponseDTO totalIncomeResponseDTO = new TotalIncomeResponseDTO();
       totalIncomeResponseDTO.setTotalIncome(totalIncome.subtract(totalExpenses));
       return totalIncomeResponseDTO;
    }
}
