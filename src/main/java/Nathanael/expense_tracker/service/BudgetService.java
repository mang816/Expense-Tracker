package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public void addBudget(Budget budget) {
        budgetRepository.save(budget);
    }

    public void saveBudget(Nathanael.expense_tracker.model.User user, String category, double limitAmount) {
        Nathanael.expense_tracker.model.Budget budget = new Nathanael.expense_tracker.model.Budget();
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budgetRepository.save(budget);
    }

    public java.util.List<Nathanael.expense_tracker.model.Budget> getBudgetsForUser(Nathanael.expense_tracker.model.User user) {
        return budgetRepository.findAll();
    }
}