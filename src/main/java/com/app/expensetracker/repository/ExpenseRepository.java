package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query(value = "select coalesce(SUM(e.amount), 0) from Expense e where e.user.id = :userId")
    BigDecimal getTotalAmountExpensesByUserId(@Param("userId") Long userId);

    @Query(value = "select e from Expense e where e.user.id = :userId")
    List<Expense> getAllExpensesByUserId(@Param("userId") Long userId);

    //find Amount Expenses By Category (Total Expenses By Category)
    @Query(value = "select coalesce(SUM(e.amount), 0) from Expense e where e.user.id = :userId and e.category.name = :categoryName")
    BigDecimal getExpensesAmountByCategoryAndUser(@Param("userId") Long userId, @Param("categoryName") String categoryName);

    //find Amount Expenses By Category (Total Expenses By Category)
    @Query(value = "select coalesce(SUM(e.amount), 0) from Expense e where e.user.id = :userId and e.category.name = :categoryName and e.date >= :startDate and e.date<= :endDate")
    BigDecimal getExpensesAmountByCategoryAndUserAndDate(@Param("userId") Long userId, @Param("categoryName") String categoryName, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query(value = "select e from Expense e where e.user.id = :userId and e.category.name = :categoryName")
    List<Expense> getExpensesByCategoryAndUser(@Param("userId") Long userId, @Param("categoryName") String categoryName);

}
