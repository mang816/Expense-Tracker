package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.SavingsGoal;
import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findByUser(User user);
}