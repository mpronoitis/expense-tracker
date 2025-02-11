package com.app.expensetracker.controller;

import com.app.expensetracker.config.JwtTokenFilter;
import com.app.expensetracker.config.JwtTokenUtil;
import com.app.expensetracker.dto.CategoryDTO;
import com.app.expensetracker.dto.request.BudgetRequestDTO;
import com.app.expensetracker.dto.response.BudgetResponseDTO;
import com.app.expensetracker.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.CoreMatchers.is;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@WebMvcTest(BudgetController.class)
@AutoConfigureMockMvc(addFilters = false) //disable security filters for the unit tests
public class BudgetControllerTest {

   @MockBean
   private BudgetService budgetService;

   @MockBean
   private JwtTokenFilter jwtTokenFilter;

   @MockBean
   private JwtTokenUtil jwtTokenUtil;

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   private List<BudgetResponseDTO> budgetResponseDTOList;

   private BudgetRequestDTO budgetRequestDTO;

   private BudgetResponseDTO budgetResponseDTO;
    @BeforeEach
    void setUp() {
        budgetRequestDTO = new BudgetRequestDTO();
        budgetRequestDTO.setCategoryName("Food & Groceries");
        budgetRequestDTO.setLimitAmount(new BigDecimal("136.78"));
        budgetRequestDTO.setStartDate(LocalDate.parse("2025-02-11"));
        budgetRequestDTO.setEndDate(LocalDate.parse("2025-02-28"));

        budgetResponseDTO = new BudgetResponseDTO();
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Food & Groceries");
        budgetResponseDTO.setCategory(categoryDTO);
        budgetResponseDTO.setLimitAmount(budgetRequestDTO.getLimitAmount());
        budgetResponseDTO.setStartDate(budgetRequestDTO.getStartDate());
        budgetResponseDTO.setEndDate(budgetRequestDTO.getEndDate());

       budgetResponseDTOList = List.of(budgetResponseDTO);
    }

    @Test
    public void givenUserId_AndBudgetId_AndCategoryName_whenGettingRemainingBudget_thenReturnRemaingBudget() throws Exception {
        Long userId = 1L;
        Long budgetId = 1L;
        String categoryName = "Food & Groceries";
        BigDecimal remainingBudget = new BigDecimal("200.36");


        given(budgetService.getRemainingBudget(userId,categoryName,budgetId)).willReturn(remainingBudget);

        //When - perform the get request
        ResultActions response =
                mockMvc.perform(get("/budget/{userId}/remaining/{budgetId}", 1L,1L).param("categoryName",categoryName).contentType(MediaType.APPLICATION_JSON));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload").value(new BigDecimal(String.valueOf(remainingBudget))));

    }

    @Test
    public void givenUserId_GetAllBudgetsOfAUser_thenReturnBudgetResponseDTO() throws Exception {
        given(budgetService.findAll(any(Long.class))).willReturn(budgetResponseDTOList);

        ResultActions response = mockMvc.perform(get("/budgets/{userId}",1L));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.size()",is(1)))
                .andExpect(jsonPath("$.payload[0].limitAmount").value(new BigDecimal(String.valueOf(budgetResponseDTOList.get(0).getLimitAmount()))))
                .andExpect(jsonPath("$.payload[0].startDate").value(budgetResponseDTOList.get(0).getStartDate().toString()))
                .andExpect(jsonPath("$.payload[0].endDate").value(budgetResponseDTOList.get(0).getEndDate().toString()))
                .andExpect(jsonPath("$.payload[0].category.name",is(budgetResponseDTOList.get(0).getCategory().getName())))
                .andExpect(jsonPath("$.payload[0].category.name",is(budgetResponseDTOList.get(0).getCategory().getName())));
    }

    @Test
    public void givenUserId_AndBudgetRequestDTO_thenReturnBudgetResponseDTO() throws Exception {
        given(budgetService.create(any(Long.class), any(BudgetRequestDTO.class))).willReturn(budgetResponseDTO);

        ResultActions response = mockMvc.perform((post("/budgets/{userId}/create",1L))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(budgetRequestDTO)));

        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.limitAmount").value(new BigDecimal(String.valueOf(budgetRequestDTO.getLimitAmount()))))
                .andExpect(jsonPath("$.payload.startDate").value(budgetRequestDTO.getStartDate().toString()))
                .andExpect(jsonPath("$.payload.endDate").value(budgetRequestDTO.getEndDate().toString()))
                .andExpect(jsonPath("$.payload.category.name",is(budgetRequestDTO.getCategoryName())));
    }


}
