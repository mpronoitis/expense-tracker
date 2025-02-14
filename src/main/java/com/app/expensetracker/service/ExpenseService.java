package com.app.expensetracker.service;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.request.ExpenseRequestDTO;
import com.app.expensetracker.dto.request.ExpenseUpdateRequestDTO;
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
import java.time.LocalDate;
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
        //expense data can not be in the future
        expenseRequestDTO.validate();
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

    @Transactional(rollbackOn = Exception.class)
    public ExpenseResponseDTO update(ExpenseUpdateRequestDTO updateRequestDTO, Long userId, Long expenseId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new GenericBadRequestException("Expense not found", ErrorType.IM_BUDGET_NOT_FOUND));

        Optional<Budget> oldBudgetForExistingCategoryAndDate = budgetRepository.findBudgetByCategoryAndUserAndDate(
                userId, expense.getCategory().getName(), expense.getDate());

        // Set payment method
        if (updateRequestDTO.getPaymentMethod() != null) {
            expense.setPaymentMethod(updateRequestDTO.getPaymentMethod());
        }

        // Set description
        if (updateRequestDTO.getDescription() != null) {
            expense.setDescription(updateRequestDTO.getDescription());
        }

        // Check if category or date is changing
        boolean isCategoryChanging = updateRequestDTO.getCategoryName() != null && !updateRequestDTO.getCategoryName().equals(expense.getCategory().getName());
        boolean isDateChanging = updateRequestDTO.getDate() != null && !updateRequestDTO.getDate().equals(expense.getDate());
        boolean isAmountChanging = updateRequestDTO.getAmount() != null && !updateRequestDTO.getAmount().equals(expense.getAmount());
        // If both category and date are changing
        if (isCategoryChanging && isDateChanging) {
            if (oldBudgetForExistingCategoryAndDate.isPresent()) {
                expense = updateExpenseCategoryAndDate(updateRequestDTO, oldBudgetForExistingCategoryAndDate.get(), expense, userId);
            } else {
                // If there's no old budget, update category and date without old budget validation
                expense = updateExpenseCategoryAndDate(updateRequestDTO, null, expense, userId);
            }
        }
        // If only category is changing
        else if (isCategoryChanging) {
            if (oldBudgetForExistingCategoryAndDate.isPresent()) {
                expense = updateExpenseCategory(expense, oldBudgetForExistingCategoryAndDate.get(), updateRequestDTO, userId);
            } else {
                // If no old budget, update category without affecting the budget
                expense = updateExpenseCategory(expense, null, updateRequestDTO, userId);
            }
        }
        // If only date is changing
        else if (isDateChanging) {
            if (updateRequestDTO.getDate().isAfter(LocalDate.now())) {
                throw new GenericBadRequestException("Expense date cannot be in the future", ErrorType.IM_INVALID_UPDATE_EXPENSE_DATE);
            }
            if (oldBudgetForExistingCategoryAndDate.isPresent()) {
                expense = updateExpenseDate(updateRequestDTO, oldBudgetForExistingCategoryAndDate.get(), expense, userId);
            } else {
                // If no old budget, update date without affecting the budget
                expense = updateExpenseDate(updateRequestDTO, null, expense, userId);
            }
        } else if (isAmountChanging) {
            if (updateRequestDTO.getAmount() != null && updateRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <=0) {
                throw new GenericBadRequestException("Expense amount must be greater than zero", ErrorType.IM_INVALID_EXPENSE_AMOUNT);
            }
            if (oldBudgetForExistingCategoryAndDate.isPresent()) {
                expense = updateExpenseAmount(updateRequestDTO, oldBudgetForExistingCategoryAndDate.get(), expense);
            } else {
                expense = updateExpenseAmount(updateRequestDTO, null, expense);
            }
        }

        return expenseMapper.toDto(expense);

    }

    @Transactional
    public Expense updateExpenseAmount(ExpenseUpdateRequestDTO updateRequestDTO, Budget oldBudgetForExistingCategoryAndDate, Expense expense) {
        if (oldBudgetForExistingCategoryAndDate != null) {

            BigDecimal remainingLimitBudgetAmount = oldBudgetForExistingCategoryAndDate.getLimitAmount();
            BigDecimal newRemainingLimitBudgetAmount = remainingLimitBudgetAmount.subtract(expense.getAmount());

            if (newRemainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericBadRequestException("Budget limit cannot be less than zero after subtracting expenses",
                        ErrorType.IM_BUDGET_EXCEEDED);
            }
            oldBudgetForExistingCategoryAndDate.setLimitAmount(newRemainingLimitBudgetAmount);
            budgetRepository.save(oldBudgetForExistingCategoryAndDate);
        }
        expense.setAmount(updateRequestDTO.getAmount());

        return expense;
    }


    @Transactional
    public Expense updateExpenseCategoryAndDate(ExpenseUpdateRequestDTO updateRequestDTO, Budget oldBudgetForExistingCategoryAndDate, Expense expense, Long userId) {

        // Find new category
        Category newCategory = categoryRepository.findCategoryByName(updateRequestDTO.getCategoryName())
                .orElseThrow(() -> new GenericBadRequestException("Category not found", ErrorType.IM_CATEGORY_NOT_FOUND));

        // Check if there is a budget for the new category and new date
        Optional<Budget> optionalNewBudget = budgetRepository.findBudgetByCategoryAndUserAndDate(
                userId, newCategory.getName(), updateRequestDTO.getDate()
        );

        if (optionalNewBudget.isPresent()) {
            Budget newBudget = optionalNewBudget.get();

            BigDecimal remainingLimitBudgetAmount = newBudget.getLimitAmount();
            BigDecimal newRemainingLimitBudgetAmount = remainingLimitBudgetAmount.subtract(expense.getAmount());

            if (newRemainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericBadRequestException("Budget limit cannot be less than zero after subtracting expenses",
                        ErrorType.IM_BUDGET_EXCEEDED);
            }

            newBudget.setLimitAmount(newRemainingLimitBudgetAmount);
            budgetRepository.save(newBudget);

            // Update the old budget's limit
            if (oldBudgetForExistingCategoryAndDate != null) {
                budgetRepository.save(oldBudgetForExistingCategoryAndDate);
            }
        }

        // Update the expense with the new category and date
        expense.setCategory(newCategory);
        expense.setDate(updateRequestDTO.getDate());

        return expense;
    }

    @Transactional
    public Expense updateExpenseDate(ExpenseUpdateRequestDTO updateRequestDTO, Budget oldBudgetForExistingCategoryAndDate, Expense expense, Long userId) {

        // Check if there is a budget for the new date
        Optional<Budget> newBudget = budgetRepository.findBudgetByCategoryAndUserAndDate(userId, expense.getCategory().getName(), updateRequestDTO.getDate());

        if (oldBudgetForExistingCategoryAndDate != null) {
            oldBudgetForExistingCategoryAndDate.setLimitAmount(oldBudgetForExistingCategoryAndDate.getLimitAmount().add(expense.getAmount()));
        }

       boolean isSameBudget = oldBudgetForExistingCategoryAndDate != null && newBudget.isPresent() && oldBudgetForExistingCategoryAndDate.getId().equals(newBudget.get().getId());
        if (isSameBudget) {
                // If they are the same, adjust the limit only once
                BigDecimal updatedLimit = oldBudgetForExistingCategoryAndDate.getLimitAmount();
                updatedLimit = updatedLimit.subtract(expense.getAmount());  // Subtract expense for new date
                if (updatedLimit.compareTo(BigDecimal.ZERO) < 0) {
                    throw new GenericBadRequestException("Budget limit cannot be less than zero after subtracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
            }
            oldBudgetForExistingCategoryAndDate.setLimitAmount(updatedLimit);
            budgetRepository.save(oldBudgetForExistingCategoryAndDate);
        } else {
            // If they are different, process the old and new budgets separately
            if (oldBudgetForExistingCategoryAndDate != null) {
                oldBudgetForExistingCategoryAndDate.setLimitAmount(
                        oldBudgetForExistingCategoryAndDate.getLimitAmount().add(expense.getAmount())
                );
                budgetRepository.save(oldBudgetForExistingCategoryAndDate);
            }
            if (newBudget.isPresent()) {
                BigDecimal remainingLimitBudgetAmount = newBudget.get().getLimitAmount();
                BigDecimal newRemainingLimitBudgetAmount = remainingLimitBudgetAmount.subtract(expense.getAmount());

                if (newRemainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                    throw new GenericBadRequestException("Budget limit cannot be less than zero after subtracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
                }

                newBudget.get().setLimitAmount(newRemainingLimitBudgetAmount);
                budgetRepository.save(newBudget.get());
            }
        }

        expense.setDate(updateRequestDTO.getDate());
        return expense;
    }

    @Transactional
    public Expense updateExpenseCategory(Expense expense, Budget oldBudgetForExistingCategoryAndDate, ExpenseUpdateRequestDTO updateRequestDTO, Long userId) {
        if (oldBudgetForExistingCategoryAndDate != null) {
            oldBudgetForExistingCategoryAndDate.setLimitAmount(oldBudgetForExistingCategoryAndDate.getLimitAmount().add(expense.getAmount()));
        }

        // Find new category
        Category newCategory = categoryRepository.findCategoryByName(updateRequestDTO.getCategoryName())
                .orElseThrow(() -> new GenericBadRequestException("Category not found", ErrorType.IM_CATEGORY_NOT_FOUND));

        expense.setCategory(newCategory);

        // Find if there is already a budget for the new category and expense date
        Optional<Budget> optionalNewBudgetForNewCategory = budgetRepository.findBudgetByCategoryAndUserAndDate(userId, newCategory.getName(), expense.getDate());
        if (optionalNewBudgetForNewCategory.isPresent()) {
            BigDecimal remainingLimitBudgetAmount = optionalNewBudgetForNewCategory.get().getLimitAmount();
            BigDecimal newRemainingLimitBudgetAmount = remainingLimitBudgetAmount.subtract(expense.getAmount());

            if (newRemainingLimitBudgetAmount.compareTo(BigDecimal.ZERO) < 0) {
                throw new GenericBadRequestException("Budget limit cannot be less than zero after subtracting expenses", ErrorType.IM_BUDGET_EXCEEDED);
            }

            optionalNewBudgetForNewCategory.get().setLimitAmount(newRemainingLimitBudgetAmount);
            budgetRepository.save(optionalNewBudgetForNewCategory.get());
        }

        if (oldBudgetForExistingCategoryAndDate != null) {
            budgetRepository.save(oldBudgetForExistingCategoryAndDate);
        }

        return expense;
    }

}
