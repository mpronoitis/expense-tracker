package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Budget;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    //find budget associated with a specific category and user
    @Query(value = "select b from Budget b where b.user.id = :userId and b.category.name = :categoryName and b.id = :budgetId")
    Optional<Budget> findBudgetByCategoryAndUser(@Param("userId") Long userId, @Param("categoryName") String categoryName, @Param("budgetId") Long budgetId);

    //find budget associated with a specific category and user and date
    @Query(value = "select b from Budget b where b.user.id = :userId and b.category.name = :categoryName and b.startDate <= :date")
    Optional<Budget> findBudgetByCategoryAndUserAndDate(@Param("userId") Long userId, @Param("categoryName") String categoryName, @Param("date") LocalDate date);

    //find all budgets of a user
    @Query(value = "select b from Budget b where b.user.id = :userId")
    List<Budget> findAllByUser(@Param("userId") Long userId);

    //find Budget by userId and budgetId
    @Query(value = "select b from Budget b where b.user.id = :userId and b.id = :budgetId")
    Optional<Budget> findBudgetByIdAndUser(@Param("userId") Long userId, @Param("budgetId") Long budgetId);
}
