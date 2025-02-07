package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    //find budget associated with a specific category and user
    @Query(value = "select b from Budget b where b.user.id = :userId and b.category.name = :categoryName")
    Optional<Budget> findBudgetByCategoryAndUser(@Param("userId") Long userId, @Param("categoryName") String categoryName);

    //find all budgets of a user
    @Query(value = "select b from Budget b where b.user.id = :userId")
    List<Budget> findAllByUser(@Param("userId") Long userId);
}
