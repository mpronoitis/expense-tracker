package com.app.expensetracker.integration;

import com.app.expensetracker.domain.Budget;
import com.app.expensetracker.domain.Category;
import com.app.expensetracker.domain.user.User;
import com.app.expensetracker.dto.request.AuthRequestDTO;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.repository.BudgetRepository;
import com.app.expensetracker.repository.CategoryRepository;
import com.app.expensetracker.repository.UserRepository;
import com.app.expensetracker.service.BudgetService;
import com.app.expensetracker.shared.rest.enumeration.ErrorType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Tag("integration-test")
@ActiveProfiles("integration")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BudgetControllerIntegrationTest {

    static final MySQLContainer MY_SQL_CONTAINER;
    static final Supplier<Object> DATABASE_DRIVER = () -> "com.mysql.cj.jdbc.Driver";

    static {
        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
                .withDatabaseName("spring-boot-integration-test")
                .withUsername("root")
                .withPassword("root");
        System.out.println("Starting MySQL container...");
        MY_SQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void setDynamicPropertySource(DynamicPropertyRegistry registry) {

        //mysql
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", DATABASE_DRIVER);
    }

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
    public void givenValidCredentials_whenGeneratingToken_thenReturnValidToken() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("temp@gmail.com");
        authRequestDTO.setPassword("Temp1234!!");

        ResultActions response = mockMvc.perform(post("/public/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequestDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").isNotEmpty());
    }

    @Test
    public void givenIValidCredentials_whenGeneratingToken_thenReturnBadRequest() throws Exception {
        AuthRequestDTO authRequestDTO = new AuthRequestDTO();
        authRequestDTO.setUsername("temp@gmail.com");
        authRequestDTO.setPassword("Temp1234!!!"); //set wrong password

        ResultActions response = mockMvc.perform(post("/public/auth/authenticate").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(authRequestDTO)));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage", is("Bad Credentials for this user")));
    }

    @Test
    @WithUserDetails(value = "temp@gmail.com", userDetailsServiceBeanName = "userClaimsService")
    public void givenUserId_AndBudgetRequestDTO_thenReturnBudgetResponseDTO() throws Exception {

        // given - precondition or setup
        BudgetRequestDTO budgetRequestDTO = new BudgetRequestDTO();
        budgetRequestDTO.setLimitAmount(new BigDecimal("150.00"));
        budgetRequestDTO.setCategoryName("Transportation");
        budgetRequestDTO.setStartDate(LocalDate.now());
        budgetRequestDTO.setEndDate(LocalDate.parse("2025-02-28"));

        //when - action or behaviour that we are going to test
        ResultActions response = mockMvc.perform((post("/budgets/{userId}/create", 1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetRequestDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.limitAmount").value(budgetRequestDTO.getLimitAmount().setScale(1)));

    }

    @Test
    @WithUserDetails(value = "temp@gmail.com", userDetailsServiceBeanName = "userClaimsService")
    public void givenUserId_AndBudgetId_AndCategoryName_whenGettingRemainingBudget_thenReturnRemaingBudget() throws Exception {

        // given - precondition or setup
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
                        .param("categoryName",category.get().getName()));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").value(savedBudget.getLimitAmount()));
    }

    @Test
    @WithUserDetails(value = "temp@gmail.com", userDetailsServiceBeanName = "userClaimsService")
    public void givenInvalidUserId_AndBudgetId_AndCategoryName_whenGettingRemainingBudget_thenReturnSecurityControlException() throws Exception {

        // given - precondition or setup
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
                .perform(get("/budget/{userId}/remaining/{budgetId}", 2L,budget.getId())
                        .param("categoryName",category.get().getName()));

        response.andDo(print()).
                andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage",is("Security Control Error: You try to get a resource that does not belongs to you")))
                .andExpect(jsonPath("$.success").value(Boolean.valueOf("false")))
                .andExpect(jsonPath("$.errorCode",is(ErrorType.IM_SECURITY_CONTROL_ERROR.getCode())));
    }



}
