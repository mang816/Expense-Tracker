package Nathanael.expense_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class BudgetRequest {

    @NotBlank(message = "Category is required")
    private String category;

    @Positive(message = "Limit amount must be greater than zero")
    private double limitAmount;

    public BudgetRequest() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }
}