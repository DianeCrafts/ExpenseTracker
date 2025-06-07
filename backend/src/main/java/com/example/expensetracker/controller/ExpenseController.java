package com.example.expensetracker.controller;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id);
    }

    @PostMapping
    public Expense createExpense(@Valid @RequestBody Expense expense) {
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
    public List<Expense> getAllArchivedExpenses(){
        return expenseService.getArchivedExpenses();
    }

    //Diane
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> getFilteredExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Expense> expensePage = expenseService.getFilteredExpenses(category, minAmount, maxAmount, startDate, endDate, page, size);

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