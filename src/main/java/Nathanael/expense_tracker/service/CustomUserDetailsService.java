package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Find the user in the database using username (matches your repository)
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Return a Spring Security User object using the data from your database
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername()) // Fixed to getUsername()
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}