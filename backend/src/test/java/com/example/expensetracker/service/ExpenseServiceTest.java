package com.example.expensetracker.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private Expense exampleExpense;

    @BeforeEach
    public void setUp() {
        exampleExpense = new Expense();
        exampleExpense.setId(1L);
        exampleExpense.setDescription("Test Expense");
        exampleExpense.setAmount(100.0);
        exampleExpense.setDate(LocalDate.now());
        exampleExpense.setDeleted(false);
        // You can set category if needed here
    }

    // ===== createExpense =====
    @Test
    public void testCreateExpense_Success() {
        when(expenseRepository.save(exampleExpense)).thenReturn(exampleExpense);

        Expense saved = expenseService.createExpense(exampleExpense);

        assertNotNull(saved);
        assertEquals("Test Expense", saved.getDescription());
        verify(expenseRepository).save(exampleExpense);
    }

    // ===== getExpenseById =====
    @Test
    public void testGetExpenseById_Found() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(exampleExpense));

        Expense found = expenseService.getExpenseById(1L);

        assertNotNull(found);
        assertEquals("Test Expense", found.getDescription());
        verify(expenseRepository).findById(1L);
    }

    @Test
    public void testGetExpenseById_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        Expense found = expenseService.getExpenseById(1L);

        assertNull(found);
        verify(expenseRepository).findById(1L);
    }

    // ===== updateExpense =====
    @Test
    public void testUpdateExpense_Success() {
        Expense updateData = new Expense();
        updateData.setDescription("Updated");
        updateData.setAmount(200.0);
        updateData.setDate(LocalDate.now().minusDays(1));

        when(expenseRepository.findById(1L)).thenReturn(Optional.of(exampleExpense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense updated = expenseService.updateExpense(1L, updateData);

        assertNotNull(updated);
        assertEquals("Updated", updated.getDescription());
        assertEquals(200.0, updated.getAmount());
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    public void testUpdateExpense_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        Expense updated = expenseService.updateExpense(1L, exampleExpense);

        assertNull(updated);
        verify(expenseRepository).findById(1L);
        verify(expenseRepository, never()).save(any());
    }

    // ===== deleteExpense =====
    @Test
    public void testDeleteExpense() {
        doNothing().when(expenseRepository).deleteById(1L);

        expenseService.deleteExpense(1L);

        verify(expenseRepository).deleteById(1L);
    }

    // ===== softDeleteExpense =====
    @Test
    public void testSoftDeleteExpense_Success() {
        exampleExpense.setDeleted(false);
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(exampleExpense));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Expense softDeleted = expenseService.softDeleteExpense(1L);

        assertTrue(softDeleted.isDeleted());
        verify(expenseRepository).findById(1L);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    public void testSoftDeleteExpense_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            expenseService.softDeleteExpense(1L);
        });

        assertEquals("Expense not found", exception.getMessage());
        verify(expenseRepository).findById(1L);
        verify(expenseRepository, never()).save(any());
    }

    // ===== getAllExpensesPaginated =====
    @Test
    public void testGetAllExpensesPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Expense> page = new PageImpl<>(List.of(exampleExpense));

        when(expenseRepository.findByIsDeletedFalse(pageable)).thenReturn(page);

        Page<Expense> result = expenseService.getAllExpensesPaginated(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(expenseRepository).findByIsDeletedFalse(pageable);
    }

    // ===== getArchivedExpenses =====
    @Test
    public void testGetArchivedExpenses() {
        Pageable pageable = PageRequest.of(0, 10);
        exampleExpense.setDeleted(true);
        Page<Expense> page = new PageImpl<>(List.of(exampleExpense));

        when(expenseRepository.findByIsDeletedTrue(pageable)).thenReturn(page);

        Page<Expense> result = expenseService.getArchivedExpenses(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(expenseRepository).findByIsDeletedTrue(pageable);
    }

    // ===== deleteAllExpenses =====
    @Test
    public void testDeleteAllExpenses() {
        doNothing().when(expenseRepository).deleteAll();

        expenseService.deleteAllExpenses();

        verify(expenseRepository).deleteAll();
    }

    // ===== archiveExpensesOlderThan30Days =====
    @Test
    public void testArchiveExpensesOlderThan30Days() {
        LocalDate cutoff = LocalDate.now().minusDays(30);
        when(expenseRepository.archiveOldExpenses(cutoff)).thenReturn(5);

        int archivedCount = expenseService.archiveExpensesOlderThan30Days();

        assertEquals(5, archivedCount);
        verify(expenseRepository).archiveOldExpenses(cutoff);
    }

    @Test
    public void testGetFilteredExpenses() {
        // Arrange - set up mock data and behavior
        String description = "Lunch";
        String category = "Food";
        Double minAmount = 10.0;
        Double maxAmount = 50.0;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Expense filteredExpense = new Expense();
        filteredExpense.setDescription("Lunch");
        filteredExpense.setAmount(25.0);
        filteredExpense.setDate(LocalDate.of(2024, 5, 20));
        filteredExpense.setDeleted(false);

        Page<Expense> mockPage = new PageImpl<>(List.of(filteredExpense));

        when(expenseRepository.findFiltered(
                description, category, minAmount, maxAmount, startDate, endDate, pageable
        )).thenReturn(mockPage);

        Page<Expense> result = expenseService.getFilteredExpenses(
                description, category, minAmount, maxAmount, startDate, endDate, page, size
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Lunch", result.getContent().get(0).getDescription());

        verify(expenseRepository).findFiltered(
                description, category, minAmount, maxAmount, startDate, endDate, pageable
        );
    }
}
