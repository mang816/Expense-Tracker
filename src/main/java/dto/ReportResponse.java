package Nathanael.expense_tracker.dto;

import java.util.Map;

public class ReportResponse {

    private double total;
    private int count;
    private double average;
    private Map<String, Double> categoryTotals;
    private Map<String, Double> monthlyTotals;

    public ReportResponse(double total, int count, double average, Map<String, Double> categoryTotals, Map<String, Double> monthlyTotals) {
        this.total = total;
        this.count = count;
        this.average = average;
        this.categoryTotals = categoryTotals;
        this.monthlyTotals = monthlyTotals;
    }

    public double getTotal() {
        return total;
    }

    public int getCount() {
        return count;
    }

    public double getAverage() {
        return average;
    }

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public Map<String, Double> getMonthlyTotals() {
        return monthlyTotals;
    }
}