package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.exception.ResourceNotFoundException;
import Nathanael.expense_tracker.exception.UsernameAlreadyExistsException;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword) {

        String cleanUsername = username == null ? "" : username.trim();

        if (cleanUsername.isEmpty() || rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required.");
        }

        if (userRepository.findByUsername(cleanUsername).isPresent()) {
            throw new UsernameAlreadyExistsException(
                    "That username is already taken. Please log in or choose a different one.");
        }

        User user = new User(cleanUsername, passwordEncoder.encode(rawPassword));

        return userRepository.save(user);
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}