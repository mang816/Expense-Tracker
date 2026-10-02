package Nathanael.expense_tracker.dto;

import Nathanael.expense_tracker.model.SavingsGoal;

import java.time.LocalDate;

public class SavingsGoalResponse {

    private Long id;
    private String name;
    private double targetAmount;
    private double currentAmount;
    private LocalDate targetDate;
    private String category;

    public SavingsGoalResponse(SavingsGoal goal) {
        this.id = goal.getId();
        this.name = goal.getName();
        this.targetAmount = goal.getTargetAmount();
        this.currentAmount = goal.getCurrentAmount();
        this.targetDate = goal.getTargetDate();
        this.category = goal.getCategory();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public String getCategory() {
        return category;
    }
}