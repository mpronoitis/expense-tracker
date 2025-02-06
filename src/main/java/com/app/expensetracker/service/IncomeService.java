package com.app.expensetracker.service;

import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.domain.Income;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.dto.request.IncomeRequestDTO;
import com.app.expensetracker.dto.response.IncomeResponseDTO;
import com.app.expensetracker.dto.response.TotalIncomeResponseDTO;
import com.app.expensetracker.error.exception.NotFoundException;
import com.app.expensetracker.mapper.IncomeMapper;
import com.app.expensetracker.repository.IncomeRepository;
import com.app.expensetracker.repository.UserRepository;
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
        User user = userRepository.findById(userClaimsDTO.getId()).orElseThrow(() -> new NotFoundException("User not found"));

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

    public TotalIncomeResponseDTO getTotalIncome() {

        UserClaimsDTO userClaimsDTO = UserClaimsService.getUserClaimsDTO();
        User user = userRepository.findById(userClaimsDTO.getId()).orElseThrow(() -> new NotFoundException("User not found"));
        Set<Income> incomes =  user.getIncomes();
        BigDecimal totalIncome = BigDecimal.ZERO;
        Set<Expense> expenses = user.getExpenses();
        if (expenses.isEmpty()) { // if expenses are empty mean that totalIncome of user are all the incomes together
            for (Income income: incomes) {
                totalIncome = totalIncome.add(income.getAmount());
            }
        } else {  //we must calculate the total income based on his expenses
                totalIncome = incomeRepository.getTotalIncomesByUserId(userClaimsDTO.getId());
            for (Expense expense: expenses) {
                totalIncome = totalIncome.subtract(expense.getAmount());
            }

        }

        TotalIncomeResponseDTO totalIncomeResponseDTO = new TotalIncomeResponseDTO();
        totalIncomeResponseDTO.setTotalIncome(totalIncome);

        return totalIncomeResponseDTO;
    }
}
