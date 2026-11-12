package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.SavingsGoal;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.SavingsGoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;

    public SavingsGoalService(SavingsGoalRepository savingsGoalRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
    }

    public List<SavingsGoal> getGoalsForUser(User user) {
        return savingsGoalRepository.findByUser(user);
    }

    public SavingsGoal createGoal(SavingsGoal goal) {
        return savingsGoalRepository.save(goal);
    }

    public void addContribution(Long id, double amount, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            goal.setCurrentAmount(goal.getCurrentAmount() + amount);
            savingsGoalRepository.save(goal);
        }
    }

    public void updateGoal(Long id, String name, double targetAmount, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            goal.setName(name);
            goal.setTargetAmount(targetAmount);
            savingsGoalRepository.save(goal);
        }
    }

    public void deleteGoal(Long id, User user) {
        SavingsGoal goal = savingsGoalRepository.findById(id).orElse(null);
        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            savingsGoalRepository.delete(goal);
        }
    }
}