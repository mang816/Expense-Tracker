package Nathanael.expense_tracker.dto;

public class BudgetResponse {

    private String category;
    private double limitAmount;
    private double spent;
    private double percentUsed;

    public BudgetResponse(String category, double limitAmount, double spent) {
        this.category = category;
        this.limitAmount = limitAmount;
        this.spent = spent;
        this.percentUsed = limitAmount > 0 ? Math.min((spent / limitAmount) * 100, 100) : 0;
    }

    public String getCategory() {
        return category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public double getSpent() {
        return spent;
    }

    public double getPercentUsed() {
        return percentUsed;
    }
}