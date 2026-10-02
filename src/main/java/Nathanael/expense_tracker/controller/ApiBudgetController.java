package Nathanael.expense_tracker.controller;

import Nathanael.expense_tracker.dto.BudgetRequest;
import Nathanael.expense_tracker.dto.BudgetResponse;
import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.BudgetService;
import Nathanael.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
public class ApiBudgetController {

    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ApiBudgetController(BudgetService budgetService, ExpenseService expenseService, UserRepository userRepository) {
        this.budgetService = budgetService;
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow();
    }

    @GetMapping
    public List<BudgetResponse> getBudgets(Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Map<String, Double> categoryTotals = expenseService.getExpensesForUser(currentUser)
                .stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        return budgetService.getBudgetsForUser(currentUser)
                .stream()
                .map(b -> new BudgetResponse(b.getCategory(), b.getLimitAmount(), categoryTotals.getOrDefault(b.getCategory(), 0.0)))
                .toList();
    }

    @PostMapping
    public BudgetResponse setBudget(@Valid @RequestBody BudgetRequest request, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        budgetService.saveBudget(currentUser, request.getCategory(), request.getLimitAmount());

        Map<String, Double> categoryTotals = expenseService.getExpensesForUser(currentUser)
                .stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        double spent = categoryTotals.getOrDefault(request.getCategory(), 0.0);

        return new BudgetResponse(request.getCategory(), request.getLimitAmount(), spent);
    }
}