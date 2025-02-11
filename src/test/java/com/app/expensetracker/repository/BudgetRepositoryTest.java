package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/*
 * @DataJpaTest will automatically configure in-memory database for testing
 * and, it will not load annotated beans into the Application Context.
 * It will only load the repository class. Tests annotated with @DataJpaTest
 * are by default transactional and roll back at the end of each test.
 */
@DataJpaTest
@ActiveProfiles("test")
public class BudgetRepositoryTest {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Budget budget;

    private User user;

    private Category category;

    @BeforeEach
    void setUp() {
        budget = new Budget();
        user = new User();
        user.setUsername("temp@gmail.com");
        user.setPassword("temppassword");
        category = new Category();
        category.setName("Temp Category");

        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(new BigDecimal("130.00"));
        budget.setStartDate(LocalDate.of(2025, 02, 10));
        budget.setEndDate(LocalDate.of(2025, 02, 28));

        categoryRepository.save(category);
        userRepository.save(user);
    }

    @Test
    void givenCategoryAndUser_whenFindBudgetByCategoryAndUser_ThenReturnBudget() {

        // given - precondition or setup
        Budget savedBudget =  budgetRepository.save(budget);

        // when - action or behaviour that we are going to test
        Optional<Budget> budgetFromDB = budgetRepository.findBudgetByCategoryAndUser(user.getId(), budget.getCategory().getName(), savedBudget.getId());

        assertThat(budgetFromDB.get()).isNotNull();
        assertThat(budgetFromDB.get().getLimitAmount()).isEqualTo(budget.getLimitAmount());
        assertThat(budgetFromDB.get().getStartDate()).isEqualTo(budget.getStartDate());
        assertThat(budgetFromDB.get().getEndDate()).isEqualTo(budget.getEndDate());
        assertThat(budgetFromDB.get().getUser().getUsername()).isEqualTo(budget.getUser().getUsername());
        assertThat(budgetFromDB.get().getCategory().getName()).isEqualTo(budget.getCategory().getName());
    }

    @Test
    void givenUserId_whenFindAllBudgetsByUser_ThenReturnAListOfBudgets() {
        // given - precondition or setup
        Budget savedBudget =  budgetRepository.save(budget);

        // when - action or behaviour that we are going to test
        List<Budget> budgets = budgetRepository.findAllByUser(savedBudget.getUser().getId());

        assertThat(budgets).isNotNull();
        assertThat(budgets.size()).isEqualTo(1);
        assertThat(budgets.get(0).getCategory().getName()).isEqualTo(category.getName());
    }
}
