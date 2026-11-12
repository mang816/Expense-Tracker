package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    public List<Budget> getBudgetsForUser(User user) {
        return budgetRepository.findByUser(user);
    }

    public void saveBudget(User user, String category, double limitAmount) {
        Budget budget = budgetRepository.findByUserAndCategory(user, category)
                .orElse(new Budget());
        budget.setUser(user);
        budget.setCategory(category);
        budget.setLimitAmount(limitAmount);
        budgetRepository.save(budget);
    }

    public void updateBudget(Long id, String category, double limitAmount, User user) {
        Budget budget = budgetRepository.findById(id).orElse(null);
        if (budget != null && budget.getUser().getId().equals(user.getId())) {
            budget.setCategory(category);
            budget.setLimitAmount(limitAmount);
            budgetRepository.save(budget);
        }
    }

    public void deleteBudget(Long id, User user) {
        Budget budget = budgetRepository.findById(id).orElse(null);
        if (budget != null && budget.getUser().getId().equals(user.getId())) {
            budgetRepository.delete(budget);
        }
    }
}