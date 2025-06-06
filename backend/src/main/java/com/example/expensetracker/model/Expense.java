package com.example.expensetracker.model;

import javax.persistence.*;
import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Entity
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "category_id") // Foreign key column
    private Category category;
    // Constructors
    public Expense() {}

    public Expense(String description, Double amount, LocalDate date, Category category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }


    //Diane
    // And include in constructor, getter, and setter
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
