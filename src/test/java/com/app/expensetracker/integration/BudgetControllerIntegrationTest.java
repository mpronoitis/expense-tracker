package com.app.expensetracker.integration;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.repository.BudgetRepository;
import com.app.expensetracker.repository.CategoryRepository;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("integration")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BudgetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
            budgetRepository.deleteAll();
    }

    @Test
    public void givenUserId_AndBudgetRequestDTO_thenReturnBudgetResponseDTO() throws Exception {

        // given - precondition or setup
        BudgetRequestDTO budgetRequestDTO = new BudgetRequestDTO();
        budgetRequestDTO.setLimitAmount(new BigDecimal("150.00"));
        budgetRequestDTO.setCategoryName("Transportation");
        budgetRequestDTO.setStartDate(LocalDate.parse("2025-02-11"));
        budgetRequestDTO.setEndDate(LocalDate.parse("2025-02-28"));
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxLHRlbXBAZ21haWwuY29tIiwiaXNzIjoibG9jYWxob3N0IiwiaWF0IjoxNzM5MjYzMTgwLCJpc0F1dGhlbnRpY2F0aW9uIjp0cnVlLCJ1c2VyQ2xhaW1zIjp7ImlkIjoxLCJ1c2VybmFtZSI6InRlbXBAZ21haWwuY29tIiwicGFzc3dvcmQiOm51bGwsImZpcnN0TmFtZSI6IlRlbXAiLCJsYXN0TmFtZSI6IlVzZXIiLCJhY2NvdW50Tm9uRXhwaXJlZCI6dHJ1ZSwiYWNjb3VudE5vbkxvY2tlZCI6dHJ1ZSwiY3JlZGVudGlhbHNOb25FeHBpcmVkIjp0cnVlLCJlbmFibGVkIjp0cnVlLCJhdXRob3JpdGllcyI6W119LCJleHAiOjE3Mzk4Njc5ODB9.VWakfKWnjIanLOK8ieNOgjYT_5Dxeewspy-7e5Y0ko-9XQJvH68FXG2k4UfgvlrTBCmbAGMG_h0IjJyLAUTleA";

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform((post("/budgets/{userId}/create", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetRequestDTO)).header("Authorization", "Bearer " + jwtToken));

        response.andDo(print());

    }

    @Test
    public void givenUserId_AndBudgetId_AndCategoryName_whenGettingRemainingBudget_thenReturnRemaingBudget() throws Exception {

        // given - precondition or setup
        String jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxLHRlbXBAZ21haWwuY29tIiwiaXNzIjoibG9jYWxob3N0IiwiaWF0IjoxNzM5MjYzMTgwLCJpc0F1dGhlbnRpY2F0aW9uIjp0cnVlLCJ1c2VyQ2xhaW1zIjp7ImlkIjoxLCJ1c2VybmFtZSI6InRlbXBAZ21haWwuY29tIiwicGFzc3dvcmQiOm51bGwsImZpcnN0TmFtZSI6IlRlbXAiLCJsYXN0TmFtZSI6IlVzZXIiLCJhY2NvdW50Tm9uRXhwaXJlZCI6dHJ1ZSwiYWNjb3VudE5vbkxvY2tlZCI6dHJ1ZSwiY3JlZGVudGlhbHNOb25FeHBpcmVkIjp0cnVlLCJlbmFibGVkIjp0cnVlLCJhdXRob3JpdGllcyI6W119LCJleHAiOjE3Mzk4Njc5ODB9.VWakfKWnjIanLOK8ieNOgjYT_5Dxeewspy-7e5Y0ko-9XQJvH68FXG2k4UfgvlrTBCmbAGMG_h0IjJyLAUTleA";
        Budget budget = new Budget();
        Optional<User> user = userRepository.findById(1L);
        Optional<Category> category = categoryRepository.findCategoryByName("Transportation");
        budget.setUser(user.get());
        budget.setCategory(category.get());
        budget.setLimitAmount(new BigDecimal("150.16"));
        budget.setStartDate(LocalDate.parse("2025-02-12"));
        budget.setEndDate(LocalDate.parse("2025-02-28"));
        Budget savedBudget = budgetRepository.save(budget);

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc
                .perform(get("/budget/{userId}/remaining/{budgetId}", user.get().getId(),budget.getId())
                        .header("Authorization", "Bearer " +jwtToken).param("categoryName",category.get().getName()));

        response.andDo(print()).andExpect(status().isOk()).andExpect(jsonPath("$.payload").value(savedBudget.getLimitAmount()));

    }



}
