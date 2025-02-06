package com.app.expensetracker.domain;

import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.enumeration.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "income")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String source;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    //we need to track the totalIncode of the user because a user can have multipe sources of income
    @Transient
    private BigDecimal totalIncome;

    @ManyToOne
    private User user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
