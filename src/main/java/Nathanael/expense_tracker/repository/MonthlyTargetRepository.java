package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.MonthlyTarget;
import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlyTargetRepository extends JpaRepository<MonthlyTarget, Long> {

    Optional<MonthlyTarget> findByUser(User user);
}