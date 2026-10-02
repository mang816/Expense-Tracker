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

    public void createGoal(SavingsGoal goal) {
        savingsGoalRepository.save(goal);
    }

    public void addContribution(Long goalId, double amount, User user) {

        SavingsGoal goal = savingsGoalRepository.findById(goalId).orElse(null);

        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            goal.setCurrentAmount(goal.getCurrentAmount() + amount);
            savingsGoalRepository.save(goal);
        }
    }

    public void deleteGoal(Long goalId, User user) {

        SavingsGoal goal = savingsGoalRepository.findById(goalId).orElse(null);

        if (goal != null && goal.getUser().getId().equals(user.getId())) {
            savingsGoalRepository.deleteById(goalId);
        }
    }
}