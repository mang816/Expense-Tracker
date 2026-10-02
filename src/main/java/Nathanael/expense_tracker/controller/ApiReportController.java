package Nathanael.expense_tracker.controller;

import Nathanael.expense_tracker.dto.ReportResponse;
import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.ExpenseService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ApiReportController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;

    public ApiReportController(ExpenseService expenseService, UserRepository userRepository) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow();
    }

    @GetMapping("/summary")
    public ReportResponse getSummary(Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        List<Expense> expenses = expenseService.getExpensesForUser(currentUser);

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        int count = expenses.size();
        double average = count > 0 ? total / count : 0;

        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        Map<String, Double> monthlyTotalsRaw = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + e.getDate().getYear(),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> monthlyTotals = expenses.stream()
                .map(e -> e.getDate().withDayOfMonth(1))
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toMap(
                        d -> d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear(),
                        d -> monthlyTotalsRaw.get(d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        return new ReportResponse(total, count, average, categoryTotals, monthlyTotals);
    }
}