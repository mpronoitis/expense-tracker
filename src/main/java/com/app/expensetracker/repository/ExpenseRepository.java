package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = "select coalesce(SUM(e.amount), 0) from Expense e where e.user.id = :userId")
    BigDecimal getTotalAmountExpensesByUserId(@Param("userId") Long userId);

    @Query(value = "select e from Expense e where e.user.id = :userId")
    List<Expense> getAllExpensesByUserId(@Param("userId") Long userId);
}
