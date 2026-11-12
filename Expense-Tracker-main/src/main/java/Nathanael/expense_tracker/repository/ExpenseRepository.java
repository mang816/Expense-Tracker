package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUser(User user);

    // ADD THIS LINE: Fetches all recurring bills for a specific user for the UI
    List<Expense> findByUserAndRecurringTrue(User user);

    List<Expense> findByRecurringTrueAndNextDueDateLessThanEqual(LocalDate date);
}