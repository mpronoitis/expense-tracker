package com.app.expensetracker.repository;

import com.app.expensetracker.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "select mu from User mu where mu.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

}
