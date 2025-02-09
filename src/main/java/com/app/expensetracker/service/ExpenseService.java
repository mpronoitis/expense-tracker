package com.app.expensetracker.service;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.request.ExpenseRequestDTO;
import com.app.expensetracker.dto.response.ExpenseResponseDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.mapper.ExpenseMapper;
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
public class ExpenseService {
    private final ExpenseMapper expenseMapper;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;

    public List<ExpenseResponseDTO> getAll(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        List<Expense> expenses = expenseRepository.getAllExpensesByUserId(userId);

        return expenseMapper.toDto(expenses);
    }

    @Transactional(rollbackOn = Exception.class)
    public ExpenseResponseDTO create(Long userId, ExpenseRequestDTO expenseRequestDTO) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        //find if the provided category exists else throw error
        Category category = categoryRepository.findCategoryByName(expenseRequestDTO.getCategoryName()).orElseThrow(() -> new GenericBadRequestException("Category not found", ErrorType.IM_CATEGORY_NOT_FOUND));

        //Find if the user has associated budgets for the specific category
        Optional<Budget> optionalBudget = budgetRepository.findBudgetByCategoryAndUserAndDate(user.getId(), category.getName(), expenseRequestDTO.getDate());
        if (optionalBudget.isPresent()) { // we must update the limit amount of the budget and also check if budget is over exceeded for this category
            BigDecimal remainingLimitBudgetAmount = optionalBudget.get().getLimitAmount();
            if (remainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericBadRequestException("Budget limit cannot be less that zero after substracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
            }
            BigDecimal newRemainingLimitBudgetAmount = remainingLimitBudgetAmount.subtract(expenseRequestDTO.getAmount());
            Budget budget = optionalBudget.get();
            if (newRemainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericBadRequestException("Budget limit cannot be less that zero after substracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
            }
            budget.setLimitAmount(newRemainingLimitBudgetAmount);
            budgetRepository.save(budget);
        }
        //Create the expense
        Expense expense = new Expense();
        expense.setDate(expenseRequestDTO.getDate());
        expense.setAmount(expenseRequestDTO.getAmount());
        expense.setDescription(expenseRequestDTO.getDescription());
        expense.setPaymentMethod(expenseRequestDTO.getPaymentMethod());
        expense.setUser(user);
        expense.setCategory(category);

        Expense savedExpense = expenseRepository.save(expense);

        return expenseMapper.toDto(savedExpense);
    }
}
