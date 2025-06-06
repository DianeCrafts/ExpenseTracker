package com.example.expensetracker.repository;

import com.example.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE "
            + "e.isDeleted = false AND "
            + "(:category IS NULL OR e.category.name = :category) AND "
            + "(:minAmount IS NULL OR e.amount >= :minAmount) AND "
            + "(:maxAmount IS NULL OR e.amount <= :maxAmount) AND "
            + "(:startDate IS NULL OR e.date >= :startDate) AND "
            + "(:endDate IS NULL OR e.date <= :endDate)")
    Page<Expense> findFiltered(@Param("category") String category,
                               @Param("minAmount") Double minAmount,
                               @Param("maxAmount") Double maxAmount,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate,
                               Pageable pageable);

    List<Expense> findByIsDeletedFalse();
    List<Expense> findByIsDeletedTrue();

    @Modifying
    @Query("UPDATE Expense e SET e.isDeleted = true WHERE e.date < :cutoffDate AND e.isDeleted = false")
    int archiveOldExpenses(@Param("cutoffDate") LocalDate cutoffDate);

}