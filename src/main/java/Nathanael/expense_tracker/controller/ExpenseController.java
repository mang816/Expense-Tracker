package Nathanael.expense_tracker.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import Nathanael.expense_tracker.model.Account;
import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.Income;
import Nathanael.expense_tracker.model.SavingsGoal;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.AccountService;
import Nathanael.expense_tracker.service.BudgetService;
import Nathanael.expense_tracker.service.ExpenseService;
import Nathanael.expense_tracker.service.IncomeService;
import Nathanael.expense_tracker.service.MonthlyTargetService;
import Nathanael.expense_tracker.service.SavingsGoalService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;
    private final AccountService accountService;
    private final IncomeService incomeService;
    private final MonthlyTargetService monthlyTargetService;

    public ExpenseController(ExpenseService expenseService, UserRepository userRepository, BudgetService budgetService, SavingsGoalService savingsGoalService, AccountService accountService, IncomeService incomeService, MonthlyTargetService monthlyTargetService) {
        this.expenseService = expenseService;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
        this.savingsGoalService = savingsGoalService;
        this.accountService = accountService;
        this.incomeService = incomeService;
        this.monthlyTargetService = monthlyTargetService;
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow();
    }

    private Map<String, String> getCategoryIcons() {
        Map<String, String> icons = new LinkedHashMap<>();
        icons.put("Food & Groceries", "🛒");
        icons.put("Utilities", "🔧");
        icons.put("Shopping", "🛍️");
        icons.put("Housing", "🏠");
        icons.put("Transportation", "🚌");
        icons.put("Healthcare", "🏥");
        icons.put("Entertainment", "🎬");
        icons.put("Career Development", "📈");
        icons.put("Travel", "✈️");
        icons.put("Charity & Gifts", "🎁");
        icons.put("Subscription", "🔁");
        icons.put("Personal Care", "💇");
        icons.put("Pet", "🐾");
        icons.put("Household Supplies", "🧴");
        icons.put("Other", "📦");
        return icons;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }


    @PostMapping("/add")
    public String addExpense(
            @Valid @ModelAttribute Expense expense,
            BindingResult result,
            Model model,
            Authentication authentication,
            @RequestParam(required = false) MultipartFile receipt) throws IOException {

        User currentUser = getCurrentUser(authentication);

        if (result.hasErrors()) {
            var expenses = expenseService.getExpensesForUser(currentUser);

            double total = expenses.stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();

            int count = expenses.size();

            double average = count > 0 ? total / count : 0;

            var categoryTotals = expenses.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getCategory,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            model.addAttribute("expenses", expenses);
            model.addAttribute("total", total);
            model.addAttribute("count", count);
            model.addAttribute("average", average);
            model.addAttribute("categoryTotals", categoryTotals);
            model.addAttribute("categoryIcons", getCategoryIcons());
            model.addAttribute("today", LocalDate.now());

            return "index";
        }

        expense.setUser(currentUser);

        if (expense.isRecurring()) {
            expense.setNextDueDate(expense.getDate());
        }

        if (receipt != null && !receipt.isEmpty()) {
            expense.setReceiptImage(receipt.getBytes());
            expense.setReceiptContentType(receipt.getContentType());
        }

        expenseService.addExpense(expense);

        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        Expense expense = expenseService.getExpenseById(id);

        if (expense != null && expense.getUser().getId().equals(currentUser.getId())) {
            expenseService.deleteExpense(id);
        }

        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        Expense expense = expenseService.getExpenseById(id);

        if (expense == null || !expense.getUser().getId().equals(currentUser.getId())) {
            return "redirect:/";
        }

        model.addAttribute("expense", expense);

        return "edit";
    }

    @PostMapping("/budget")
    public String saveBudget(
            @RequestParam String category,
            @RequestParam double limitAmount,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        budgetService.saveBudget(currentUser, category, limitAmount);

        return "redirect:/";
    }

    @PostMapping("/goals")
    public String createGoal(
            @RequestParam String name,
            @RequestParam double targetAmount,
            @RequestParam(required = false) String targetDate,
            @RequestParam(required = false) String category,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        LocalDate parsedDate = (targetDate != null && !targetDate.isBlank()) ? LocalDate.parse(targetDate) : null;
        String parsedCategory = (category != null && !category.isBlank()) ? category : null;

        SavingsGoal goal = new SavingsGoal(name, targetAmount, parsedDate, parsedCategory, currentUser);
        savingsGoalService.createGoal(goal);

        return "redirect:/";
    }

    @PostMapping("/goals/{id}/contribute")
    public String contributeToGoal(
            @PathVariable Long id,
            @RequestParam double amount,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        savingsGoalService.addContribution(id, amount, currentUser);

        return "redirect:/";
    }

    @GetMapping("/goals/{id}/delete")
    public String deleteGoal(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        savingsGoalService.deleteGoal(id, currentUser);

        return "redirect:/";
    }

    @PostMapping("/accounts")
    public String createAccount(
            @RequestParam String name,
            @RequestParam String type,
            @RequestParam double balance,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Account account = new Account(name, type, balance, currentUser);
        accountService.addAccount(account);

        return "redirect:/";
    }

    @GetMapping("/accounts/{id}/delete")
    public String deleteAccount(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        accountService.deleteAccount(id, currentUser);

        return "redirect:/";
    }

    @PostMapping("/income")
    public String addIncome(
            @RequestParam String source,
            @RequestParam double amount,
            @RequestParam String date,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        Income income = new Income(source, amount, LocalDate.parse(date), currentUser);
        incomeService.addIncome(income);

        return "redirect:/";
    }

    @GetMapping("/income/{id}/delete")
    public String deleteIncome(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        incomeService.deleteIncome(id, currentUser);

        return "redirect:/";
    }

    @PostMapping("/target")
    public String saveTarget(
            @RequestParam double targetSurplus,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        monthlyTargetService.saveTarget(currentUser, targetSurplus);

        return "redirect:/";
    }

    @GetMapping("/export/csv")
    public void exportCsv(Authentication authentication, HttpServletResponse response) throws IOException {

        User currentUser = getCurrentUser(authentication);
        var expenses = expenseService.getExpensesForUser(currentUser);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");

        PrintWriter writer = response.getWriter();

        writer.println("Description,Amount,Category,Date");

        for (Expense expense : expenses) {
            writer.println(
                    escapeCsv(expense.getDescription()) + "," +
                            expense.getAmount() + "," +
                            escapeCsv(expense.getCategory()) + "," +
                            expense.getDate()
            );
        }

        writer.flush();
    }

    @GetMapping("/export/pdf")
    public void exportPdf(Authentication authentication, HttpServletResponse response) throws IOException {

        User currentUser = getCurrentUser(authentication);
        var expenses = expenseService.getExpensesForUser(currentUser);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Expense Report - " + currentUser.getUsername(), titleFont);
            title.setSpacingAfter(15);
            document.add(title);

            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

            Font summaryFont = new Font(Font.HELVETICA, 11, Font.NORMAL);
            Paragraph summary = new Paragraph(
                    "Total expenses: " + expenses.size() + "   |   Total spent: ₦" +
                            String.format("%,.2f", total),
                    summaryFont
            );
            summary.setSpacingAfter(15);
            document.add(summary);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 1.5f, 1.5f, 1.5f});

            Font headerFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);

            String[] headers = {"Description", "Amount", "Category", "Date"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(new Color(79, 70, 229));
                cell.setPadding(8);
                table.addCell(cell);
            }

            Font cellFont = new Font(Font.HELVETICA, 10, Font.NORMAL);

            for (Expense expense : expenses) {
                table.addCell(new Phrase(expense.getDescription(), cellFont));
                table.addCell(new Phrase("₦" + String.format("%,.2f", expense.getAmount()), cellFont));
                table.addCell(new Phrase(expense.getCategory(), cellFont));
                table.addCell(new Phrase(expense.getDate().toString(), cellFont));
            }

            document.add(table);

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            document.close();
        }
    }

    @GetMapping("/receipt/{id}")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id, Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        Expense expense = expenseService.getExpenseById(id);

        if (expense == null || !expense.getUser().getId().equals(currentUser.getId()) || expense.getReceiptImage() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(expense.getReceiptContentType()))
                .body(expense.getReceiptImage());
    }

    @GetMapping("/")
    public String home(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);

        var expenses = expenseService.getExpensesForUser(currentUser);

        if (search != null && !search.isBlank()) {
            expenses = expenses.stream()
                    .filter(e -> e.getDescription()
                            .toLowerCase()
                            .contains(search.toLowerCase()))
                    .toList();
        }

        if (category != null && !category.isBlank()) {
            expenses = expenses.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        double total = expenses.stream()
                .mapToDouble(Expense::getAmount)
                .sum();

        int count = expenses.size();

        double average = count > 0 ? total / count : 0;

        var categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> monthlyTotalsRaw = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                                + " " + e.getDate().getYear(),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> monthlyTotals = expenses.stream()
                .map(e -> e.getDate().withDayOfMonth(1))
                .distinct()
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toMap(
                        d -> d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear(),
                        d -> monthlyTotalsRaw.get(
                                d.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + d.getYear()),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));

        // Budgets: compute spent vs limit per category
        var budgets = budgetService.getBudgetsForUser(currentUser);

        var budgetProgress = budgets.stream()
                .collect(Collectors.toMap(
                        Budget::getCategory,
                        b -> {
                            double spent = categoryTotals.getOrDefault(b.getCategory(), 0.0);
                            double percent = b.getLimitAmount() > 0
                                    ? Math.min((spent / b.getLimitAmount()) * 100, 100)
                                    : 0;
                            return new double[]{spent, b.getLimitAmount(), percent};
                        }
                ));

        // Month-over-month comparison
        LocalDate currentMonthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);

        Map<String, Double> currentMonthTotals = expenses.stream()
                .filter(e -> !e.getDate().isBefore(currentMonthStart))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, Double> previousMonthTotals = expenses.stream()
                .filter(e -> !e.getDate().isBefore(previousMonthStart) && e.getDate().isBefore(currentMonthStart))
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                ));

        Map<String, double[]> monthComparison = new LinkedHashMap<>();

        for (String cat : currentMonthTotals.keySet()) {
            double current = currentMonthTotals.getOrDefault(cat, 0.0);
            double previous = previousMonthTotals.getOrDefault(cat, 0.0);
            double percentChange = previous > 0 ? ((current - previous) / previous) * 100 : (current > 0 ? 100 : 0);
            monthComparison.put(cat, new double[]{current, previous, percentChange});
        }

        // Savings goals
        List<SavingsGoal> savingsGoals = savingsGoalService.getGoalsForUser(currentUser);

        // Accounts
        List<Account> accounts = accountService.getAccountsForUser(currentUser);

        // Income
        List<Income> incomeList = incomeService.getIncomeForUser(currentUser);
        double totalIncome = incomeList.stream().mapToDouble(Income::getAmount).sum();

        // Monthly Report: this month's income vs expenses vs target
        double currentMonthExpenseTotal = currentMonthTotals.values().stream().mapToDouble(Double::doubleValue).sum();

        double currentMonthIncomeTotal = incomeList.stream()
                .filter(inc -> !inc.getDate().isBefore(currentMonthStart))
                .mapToDouble(Income::getAmount)
                .sum();

        double netSurplus = currentMonthIncomeTotal - currentMonthExpenseTotal;
        double targetSurplus = monthlyTargetService.getTargetForUser(currentUser);
        double targetGap = targetSurplus - netSurplus;

        model.addAttribute("currentMonthIncomeTotal", currentMonthIncomeTotal);
        model.addAttribute("currentMonthExpenseTotal", currentMonthExpenseTotal);
        model.addAttribute("netSurplus", netSurplus);
        model.addAttribute("targetSurplus", targetSurplus);
        model.addAttribute("targetGap", targetGap);
        model.addAttribute("currentMonthLabel", currentMonthStart.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentMonthStart.getYear());

        model.addAttribute("incomeList", incomeList);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("accounts", accounts);
        model.addAttribute("savingsGoals", savingsGoals);
        model.addAttribute("monthComparison", monthComparison);
        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("count", count);
        model.addAttribute("average", average);
        model.addAttribute("categoryTotals", categoryTotals);
        model.addAttribute("categoryIcons", getCategoryIcons());
        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("budgetProgress", budgetProgress);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);

        return "index";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(
            @PathVariable Long id,
            @ModelAttribute Expense expense,
            Authentication authentication) {

        User currentUser = getCurrentUser(authentication);
        Expense existing = expenseService.getExpenseById(id);

        if (existing != null && existing.getUser().getId().equals(currentUser.getId())) {
            expenseService.updateExpense(id, expense);
        }

        return "redirect:/";
    }
}