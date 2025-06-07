package com.example.expensetracker.controller;

import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.CategoryService;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Page;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;
    @Autowired
    private CategoryService categoryService;

//    @GetMapping
//    public List<Expense> getAllExpenses() {
//        return expenseService.getAllExpenses();
//    }


    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllExpensesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Expense> expensePage = expenseService.getAllExpensesPaginated(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", expensePage.getContent());
        response.put("currentPage", expensePage.getNumber());
        response.put("totalItems", expensePage.getTotalElements());
        response.put("totalPages", expensePage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

//    @PostMapping
//    public Expense createExpense(@Valid @RequestBody Expense expense) {
//        return expenseService.createExpense(expense);
//    }

    @PostMapping
    public Expense createExpense(@Valid @RequestBody Expense expenseDto) {
        Category category = categoryService.getCategoryById(expenseDto.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Expense expense = new Expense(
                expenseDto.getDescription(),
                expenseDto.getAmount(),
                expenseDto.getDate(),
                category
        );
        return expenseService.createExpense(expense);
    }


    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @Valid @RequestBody Expense expense) {
        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);

    }

    @DeleteMapping("/softDelete/{id}")
    public void softDeleteExpense(@PathVariable Long id) {
        expenseService.softDeleteExpense(id);
    }




    //Diane
    @DeleteMapping()
    public ResponseEntity<Void> deleteAllExpenses() {
        expenseService.deleteAllExpenses();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/archived")
    public ResponseEntity<Map<String, Object>> getArchivedExpensesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Expense> expensePage = expenseService.getArchivedExpenses(PageRequest.of(page, size));

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", expensePage.getContent());
        response.put("currentPage", expensePage.getNumber());
        response.put("totalItems", expensePage.getTotalElements());
        response.put("totalPages", expensePage.getTotalPages());

        return ResponseEntity.ok(response);
    }


    //Diane
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getFilteredExpenses(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Expense> expensePage = expenseService.getFilteredExpenses(
                description, category, minAmount, maxAmount, startDate, endDate, page, size
        );

        Map<String, Object> response = new HashMap<>();
        response.put("expenses", expensePage.getContent());
        response.put("currentPage", expensePage.getNumber());
        response.put("totalItems", expensePage.getTotalElements());
        response.put("totalPages", expensePage.getTotalPages());

        return ResponseEntity.ok(response);
    }



//    @PostMapping("/archiveOld")
//    public ResponseEntity<String> archiveOldExpenses() {
//        int count = expenseService.archiveExpensesOlderThan30Days();
//        return ResponseEntity.ok(count + " expenses archived.");
//    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void autoArchive() {
        expenseService.archiveExpensesOlderThan30Days();
    }


}