package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query(value = "select inr from Income inr where inr.user.id = :userId")
    List<Income> findIncomesByUserId(@Param("userId") Long id);

    @Query(value = "select COALESCE(SUM(inr.amount), 0) from Income inr where inr.user.id = :userId")
    BigDecimal getTotalIncomesByUserId(@Param("userId") Long userId);

}
