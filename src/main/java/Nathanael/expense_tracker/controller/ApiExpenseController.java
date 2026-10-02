package Nathanael.expense_tracker.controller;

import Nathanael.expense_tracker.dto.ExpenseRequest;
import Nathanael.expense_tracker.dto.ExpenseResponse;
import Nathanael.expense_tracker.exception.AccessDeniedCustomException;
import Nathanael.expense_tracker.exception.ResourceNotFoundException;
import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ApiExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ApiExpenseController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow();
    }

    private Expense getOwnedExpenseOrThrow(Long id, User currentUser) {

        Expense expense = expenseService.getExpenseById(id);

        if (expense == null) {
            throw new ResourceNotFoundException("Expense not found with id " + id);
        }

        if (!expense.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedCustomException("You do not have access to this expense");
        }

        return expense;
    }

    @GetMapping
    public List<ExpenseResponse> getAllExpenses(Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        return expenseService.getExpensesForUser(currentUser)
                .stream()
                .map(ExpenseResponse::new)
                .toList();
    }

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        Expense expense = getOwnedExpenseOrThrow(id, currentUser);

        return new ExpenseResponse(expense);
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setRecurring(request.isRecurring());
        expense.setRecurrenceFrequency(request.getRecurrenceFrequency());
        expense.setUser(currentUser);

        if (expense.isRecurring()) {
            expense.setNextDueDate(expense.getDate());
        }

        Expense saved = expenseService.addExpense(expense);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ExpenseResponse(saved));
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        getOwnedExpenseOrThrow(id, currentUser);

        Expense updatedData = new Expense();
        updatedData.setDescription(request.getDescription());
        updatedData.setAmount(request.getAmount());
        updatedData.setCategory(request.getCategory());
        updatedData.setDate(request.getDate());

        Expense saved = expenseService.updateExpense(id, updatedData);

        return new ExpenseResponse(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        getOwnedExpenseOrThrow(id, currentUser);

        expenseService.deleteExpense(id);

        return ResponseEntity.noContent().build();
    }
}