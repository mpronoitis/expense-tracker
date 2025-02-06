package com.app.expensetracker.repository;

import com.app.expensetracker.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select c from Category c where c.name = :name")
    Optional<Category> findCategoryByName(@Param("name") String name);
}
