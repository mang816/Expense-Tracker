package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUser(User user);

    Optional<Budget> findByUserAndCategory(User user, String category);
}