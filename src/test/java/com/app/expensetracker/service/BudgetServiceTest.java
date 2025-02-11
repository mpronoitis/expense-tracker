package com.app.expensetracker.service;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.Expense;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.CategoryDTO;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.enumeration.PaymentMethod;
import com.app.expensetracker.error.exception.GenericBadRequestException;
import com.app.expensetracker.mapper.BudgetMapper;
import com.app.expensetracker.repository.BudgetRepository;
import com.app.expensetracker.repository.CategoryRepository;
import com.app.expensetracker.repository.ExpenseRepository;
import com.app.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.BDDMockito.given;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BudgetMapper budgetMapper;

    @InjectMocks
    private BudgetService budgetService;

    private BudgetRequestDTO budgetRequestDTO;

    private BudgetResponseDTO budgetResponseDTO;

    private Budget budget;

    private Category category;

    private User user;

    private CategoryDTO categoryDTO;

    private Expense expense;


    @BeforeEach
    void setUp() {
        //create the category
        category = new Category();
        category.setId(1L);
        category.setName("Transportation");

        //create categoryDTO
        categoryDTO = new CategoryDTO();
        categoryDTO.setName("Transportation");

        //Create An Expense
        expense = new Expense();
        expense.setCategory(category);
        expense.setAmount(new BigDecimal("10.36"));
        expense.setDate(LocalDate.parse("2025-02-11"));
        expense.setId(1L);
        expense.setUser(user);
        expense.setDescription("Fee Ticket");
        expense.setPaymentMethod(PaymentMethod.CASH);

        //Create a User
        user = new User();
        Set<Expense> expenses = new HashSet<>();
        expenses.add(expense);
        user.setId(1L);
        user.setUsername("temp@gmail.com");
        user.setPassword("$2a$10$Ti6XFJrvHYaUSMSfuTzJe.fpSKPtW/oIpPJMpK0TJP9FUT867OaYu");
        user.setExpenses(expenses);

        budget = new Budget();
        budget.setId(1L);
        budget.setStartDate(LocalDate.parse("2025-02-10"));
        budget.setEndDate(LocalDate.parse("2025-02-28"));
        budget.setCategory(category);
        budget.setLimitAmount(new BigDecimal("50.00"));
        budget.setUser(user);

        budgetRequestDTO = new BudgetRequestDTO();
        budgetRequestDTO.setLimitAmount(new BigDecimal("50.00"));
        budgetRequestDTO.setStartDate(LocalDate.parse("2025-02-10"));
        budgetRequestDTO.setEndDate(LocalDate.parse("2025-02-28"));
        budgetRequestDTO.setCategoryName("Transportation");

        budgetResponseDTO = new BudgetResponseDTO();
        budgetResponseDTO.setStartDate(LocalDate.parse("2025-02-10"));
        budgetResponseDTO.setEndDate((LocalDate.parse("2025-02-28")));
        budgetResponseDTO.setLimitAmount(new BigDecimal("50.00").subtract(expense.getAmount()));
        budgetResponseDTO.setCategory(categoryDTO);
    }

    @Test
    public void givenBudgetRequestDTO_AndUserId_ThenReturnBudgetResponseDTO() {
        Long userId = 1L;
        String categoryName = "Transportation";
        BigDecimal totalExpensesForCategory = new BigDecimal("10.36");
        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));
        given(categoryRepository.findCategoryByName(categoryName)).willReturn(Optional.ofNullable(category));
        //given(expenseRepository.getExpensesAmountByCategoryAndUserAndDate(1L,"Transportation",any(LocalDate.class),any(LocalDate.class))).willReturn(totalExpensesForCategory);
        given(expenseRepository.getExpensesAmountByCategoryAndUserAndDate(eq(userId),eq(categoryName),any(LocalDate.class),any(LocalDate.class))).willReturn(totalExpensesForCategory);
        given(budgetRepository.save(any(Budget.class))).willReturn(budget);
        given(budgetMapper.toDto(any(Budget.class))).willReturn(budgetResponseDTO);


        BudgetResponseDTO result = budgetService.create(1L,budgetRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getLimitAmount()).isEqualTo(budgetResponseDTO.getLimitAmount());
        assertThat(result.getStartDate()).isEqualTo(budgetResponseDTO.getStartDate());
        assertThat(result.getEndDate()).isEqualTo(budgetResponseDTO.getEndDate());
        assertThat(result.getCategory().getName()).isEqualTo(budgetResponseDTO.getCategory().getName());

        verify(userRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
        verify(expenseRepository, times(1)).getExpensesAmountByCategoryAndUserAndDate(eq(userId), eq(categoryName),any(LocalDate.class),any(LocalDate.class));
        verify(budgetRepository, times(1)).save(any(Budget.class));
        verify(budgetMapper, times(1)).toDto(any(Budget.class));


    }

    @Test
    public void givenInvalidUserId_WhenCreatingBudget_ThenThrowException() {
        Long invalidUserId = 999L;
        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());
        GenericBadRequestException exception = assertThrows(GenericBadRequestException.class, () -> {
            budgetService.create(invalidUserId, budgetRequestDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("User not found");
        verify(userRepository, times(1)).findById(invalidUserId);
        verify(budgetRepository, times(0)).save(any(Budget.class));
        verify(budgetMapper, times(0)).toDto(any(Budget.class));
    }

    @Test
    public void givenInvalidLimitAmountForBudget_WhenCreatingBudget_ThenThrowException() {
        Long userId = 1L;
        String categoryName = "Transportation";
        BigDecimal existingExpenses = new BigDecimal("200.00");
        BigDecimal limitAmountForBudget = new BigDecimal("100.00");

        budgetRequestDTO.setCategoryName(categoryName);
        budgetRequestDTO.setLimitAmount(limitAmountForBudget);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(categoryRepository.findCategoryByName(categoryName)).willReturn(Optional.of(category));
        given(expenseRepository.getExpensesAmountByCategoryAndUserAndDate(eq(userId), eq(categoryName), any(LocalDate.class), any(LocalDate.class)))
                .willReturn(existingExpenses);

        GenericBadRequestException exception = assertThrows(GenericBadRequestException.class, () -> {
            budgetService.create(userId, budgetRequestDTO);
        });

        assertThat(exception.getMessage()).isEqualTo("Budget limit cannot be less that zero after substracting expenses");

        verify(userRepository, times(1)).findById(userId);
        verify(categoryRepository, times(1)).findCategoryByName(categoryName);
        verify(expenseRepository, times(1)).getExpensesAmountByCategoryAndUserAndDate(eq(userId), eq(categoryName), any(LocalDate.class), any(LocalDate.class));
        verify(budgetRepository, times(0)).save(any(Budget.class));
    }

    @Test
    public void givenValidUserId_AndValidCategoryName_AndValidBidgetId_thenReturnRemainingBudget() {
        Long userId = 1L;
        String categoryName = "Transportation";
        Long budgetId = 2L;

        given(budgetRepository.findBudgetByCategoryAndUser(userId,categoryName,budgetId)).willReturn(Optional.ofNullable(budget));

        BigDecimal result = budgetService.getRemainingBudget(userId, categoryName, budgetId);

        assertThat(result).isNotNull();

     verify(budgetRepository, times(1)).findBudgetByCategoryAndUser(userId, categoryName, budgetId);

    }

    @Test
    public void givenInvalidBudgetId_whenGettingRemainingBudgetForACatogory_thenThrowException() {
        Long userId = 1L;
        String categoryName = "Transportation";
        Long invalidBudgetId = 2L;


        given(budgetRepository.findBudgetByCategoryAndUser(userId,categoryName,invalidBudgetId)).willReturn(Optional.empty());

        GenericBadRequestException exception = assertThrows(GenericBadRequestException.class, () -> {
            budgetService.getRemainingBudget(userId, categoryName,invalidBudgetId);
        });

        assertThat(exception.getMessage()).isEqualTo("Budget not found");

        verify(budgetRepository, times(1)).findBudgetByCategoryAndUser(userId, categoryName, invalidBudgetId);
    }
}
