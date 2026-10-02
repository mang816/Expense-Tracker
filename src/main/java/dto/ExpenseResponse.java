package Nathanael.expense_tracker.dto;

import Nathanael.expense_tracker.model.Expense;

import java.time.LocalDate;

public class ExpenseResponse {

    private Long id;
    private String description;
    private double amount;
    private String category;
    private LocalDate date;
    private boolean recurring;
    private String recurrenceFrequency;
    private LocalDate nextDueDate;
    private boolean hasReceipt;

    public ExpenseResponse(Expense expense) {
        this.id = expense.getId();
        this.description = expense.getDescription();
        this.amount = expense.getAmount();
        this.category = expense.getCategory();
        this.date = expense.getDate();
        this.recurring = expense.isRecurring();
        this.recurrenceFrequency = expense.getRecurrenceFrequency();
        this.nextDueDate = expense.getNextDueDate();
        this.hasReceipt = expense.getReceiptImage() != null;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public String getRecurrenceFrequency() {
        return recurrenceFrequency;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public boolean isHasReceipt() {
        return hasReceipt;
    }
}