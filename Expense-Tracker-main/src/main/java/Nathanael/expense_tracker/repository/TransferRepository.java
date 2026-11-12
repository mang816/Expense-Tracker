package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.Transfer;
import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    List<Transfer> findByUserOrderByDateDesc(User user);
}