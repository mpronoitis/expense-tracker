package com.app.expensetracker.service;

import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.UserClaimsDTO;
import com.app.expensetracker.dto.request.ExpenseRequestDTO;
import com.app.expensetracker.dto.response.ExpenseResponseDTO;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.mapper.ExpenseMapper;
import com.app.expensetracker.repository.CategoryRepository;
import com.app.expensetracker.repository.ExpenseRepository;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseMapper expenseMapper;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public List<ExpenseResponseDTO> getAll(Long userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        List<Expense> expenses = expenseRepository.getAllExpensesByUserId(userId);

        return expenseMapper.toDto(expenses);
    }

    @Transactional(rollbackOn = Exception.class)
    public ExpenseResponseDTO create(ExpenseRequestDTO expenseRequestDTO) {
        UserClaimsDTO userClaimsDTO = UserClaimsService.getUserClaimsDTO();
        User user = userRepository.findById(userClaimsDTO.getId()).orElseThrow(() -> new GenericBadRequestException("User not found", ErrorType.IM_USER_NOT_FOUND));
        //find if the provided category exists else throw error
        Category category = categoryRepository.findCategoryByName(expenseRequestDTO.getCategoryName()).orElseThrow(() -> new GenericBadRequestException("Category not found", ErrorType.IM_CATEGORY_NOT_FOUND));

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
