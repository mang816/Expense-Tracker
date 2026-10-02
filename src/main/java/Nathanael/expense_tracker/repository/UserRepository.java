package Nathanael.expense_tracker.repository;

import Nathanael.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // This allows us to look up a user by their username during login
    Optional<User> findByUsername(String username);
}