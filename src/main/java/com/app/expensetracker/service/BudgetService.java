package com.app.expensetracker.service;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.request.BudgetupdateRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.mapper.BudgetMapper;
import com.app.expensetracker.repository.BudgetRepository;
import com.app.expensetracker.repository.CategoryRepository;
import com.app.expensetracker.repository.ExpenseRepository;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final BudgetMapper budgetMapper;
    
    @Transactional(rollbackOn = Exception.class)
    public BudgetResponseDTO create(Long userId,BudgetRequestDTO budgetRequestDTO) {

        budgetRequestDTO.validate(); //check if startDate and endDate of budget period are in the same month, if not throw an exception
        //Create the Budget
        Budget budget = new Budget();
        //check if user exists
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        //check if specified category exists
        Category category = categoryRepository.findCategoryByName(budgetRequestDTO.getCategoryName()).orElseThrow(() -> new GenericBadRequestException("Category not found", ErrorType.IM_CATEGORY_NOT_FOUND));
        BigDecimal limitAmountForBudget = budgetRequestDTO.getLimitAmount();
        BigDecimal totalExpensesForCategory = expenseRepository.getExpensesAmountByCategoryAndUserAndDate(userId, category.getName(), budgetRequestDTO.getStartDate(), budgetRequestDTO.getStartDate());
        if (totalExpensesForCategory != null && totalExpensesForCategory.compareTo(BigDecimal.ZERO) > 0 ) { // user has already expenses for this category
            limitAmountForBudget = limitAmountForBudget.subtract(totalExpensesForCategory);
        }

        if (limitAmountForBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new GenericBadRequestException("Budget limit cannot be less that zero after substracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
        }
        budget.setCategory(category);
        budget.setUser(user);
        budget.setLimitAmount(limitAmountForBudget);
        budget.setStartDate(budgetRequestDTO.getStartDate());
        budget.setEndDate(budgetRequestDTO.getEndDate());
        
        return budgetMapper.toDto(budgetRepository.save(budget));
        
    }

    //find the remaing budget for a specific category
    public BigDecimal getRemainingBudget(Long userId, String categoryName, Long budgetId) {
       Budget budget = budgetRepository.findBudgetByCategoryAndUser(userId, categoryName, budgetId).orElseThrow(() -> new GenericBadRequestException("Budget not found", ErrorType.IM_BUDGET_NOT_FOUND));

        return budget.getLimitAmount();
    }

    public List<BudgetResponseDTO> findAll(Long userId) {

        return budgetMapper.toDto(budgetRepository.findAllByUser(userId));
    }

    public BudgetResponseDTO update(Long userId, Long budgetId, BudgetupdateRequestDTO budgetupdateRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        Budget budget = budgetRepository.findBudgetByIdAndUser(userId, budgetId).orElseThrow(() -> new GenericBadRequestException("Budget not found", ErrorType.IM_BUDGET_NOT_FOUND));

        //update the budget
        if (budgetupdateRequestDTO.getLimitAmount() !=null) {
            budget.setLimitAmount(budgetupdateRequestDTO.getLimitAmount());
        }
        //validate startDate and endDate if provided
        if (budgetupdateRequestDTO.getStartDate() != null && budgetupdateRequestDTO.getEndDate() != null) {
            budgetupdateRequestDTO.validate();
            budget.setStartDate(budgetupdateRequestDTO.getStartDate());
            budget.setEndDate(budgetupdateRequestDTO.getEndDate());
        }

        Budget savedBudget = budgetRepository.save(budget);

        return budgetMapper.toDto(savedBudget);
    }

    public String delete(Long userId, Long budgetId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found",ErrorType.IM_USER_NOT_FOUND));
        Budget budget = budgetRepository.findBudgetByIdAndUser(userId, budgetId).orElseThrow(() -> new GenericBadRequestException("Budget not found", ErrorType.IM_BUDGET_NOT_FOUND));

        budgetRepository.delete(budget);

        return "Budget Delete successfully";
    }
}
