package Nathanael.expense_tracker.controller;

import Nathanael.expense_tracker.dto.SavingsGoalRequest;
import Nathanael.expense_tracker.dto.SavingsGoalResponse;
import Nathanael.expense_tracker.model.SavingsGoal;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.SavingsGoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class ApiGoalController {

    private final SavingsGoalService savingsGoalService;
    private final UserRepository userRepository;

    public ApiGoalController(SavingsGoalService savingsGoalService, UserRepository userRepository) {
        this.savingsGoalService = savingsGoalService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow();
    }

    @GetMapping
    public List<SavingsGoalResponse> getGoals(Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return savingsGoalService.getGoalsForUser(currentUser)
                .stream()
                .map(SavingsGoalResponse::new)
                .toList();
    }

    @PostMapping
    public ResponseEntity<Void> createGoal(@Valid @RequestBody SavingsGoalRequest request, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        SavingsGoal goal = new SavingsGoal(request.getName(), request.getTargetAmount(), request.getTargetDate(), request.getCategory(), currentUser);
        savingsGoalService.createGoal(goal);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/contribute")
    public ResponseEntity<Void> contribute(@PathVariable Long id, @RequestBody Map<String, Double> body, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        double amount = body.getOrDefault("amount", 0.0);
        savingsGoalService.addContribution(id, amount, currentUser);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        savingsGoalService.deleteGoal(id, currentUser);

        return ResponseEntity.noContent().build();
    }
}