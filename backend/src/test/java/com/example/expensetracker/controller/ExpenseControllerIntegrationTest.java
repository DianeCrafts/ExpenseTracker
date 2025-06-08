package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.CategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExpenseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long categoryId;

    @BeforeEach
    public void setup() {
        if (categoryId == null) {
            Category category = new Category();
            category.setName("Test Category");
            category = categoryRepository.save(category);
            categoryId = category.getId();
        }
    }

    @Test
    @Order(1)
    public void testCreateExpenseIntegration() throws Exception {
        Expense expense = new Expense();
        expense.setDescription("Integration Lunch");
        expense.setAmount(22.5);
        expense.setDate(LocalDate.now());

        Category category = new Category();
        category.setId(categoryId);
        expense.setCategory(category);

        mockMvc.perform(post("/api/expenses")
                        .with(httpBasic("admin", "admin"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(expense)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Integration Lunch"))
                .andExpect(jsonPath("$.amount").value(22.5));
    }
}
