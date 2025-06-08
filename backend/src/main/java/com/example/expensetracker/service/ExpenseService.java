package com.example.expensetracker.service;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;


@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

//    public List<Expense> getAllExpenses() {
//        return expenseRepository.findByIsDeletedFalse();
//    }

    public Page<Expense> getAllExpensesPaginated(Pageable pageable) {
        return expenseRepository.findByIsDeletedFalse(pageable);
    }
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense updateExpense(Long id, Expense expense) {
        Expense existing = getExpenseById(id);
        if (existing != null) {
            existing.setDescription(expense.getDescription());
            existing.setAmount(expense.getAmount());
            existing.setDate(expense.getDate());
            return expenseRepository.save(existing);
        }
        return null;
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    //Diane
    public Expense softDeleteExpense(Long id){
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        expense.setDeleted(true);
        return expenseRepository.save(expense);
    }



    public Page<Expense> getArchivedExpenses(Pageable pageable){
        return expenseRepository.findByIsDeletedTrue(pageable);
    }

    //Diane
    public void deleteAllExpenses() {
        expenseRepository.deleteAll();
    }

    public Page<Expense> getFilteredExpenses(String description, String category, Double minAmount,
                                             Double maxAmount, LocalDate startDate, LocalDate endDate,
                                             int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return expenseRepository.findFiltered(description, category, minAmount, maxAmount, startDate, endDate, pageable);
    }

    @Transactional
    public int archiveExpensesOlderThan30Days() {
        LocalDate cutoffDate = LocalDate.now().minusDays(30);
        return expenseRepository.archiveOldExpenses(cutoffDate);
    }




}