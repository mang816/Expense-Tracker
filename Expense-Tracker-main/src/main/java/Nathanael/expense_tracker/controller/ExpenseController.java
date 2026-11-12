package Nathanael.expense_tracker.controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import Nathanael.expense_tracker.model.Account;
import Nathanael.expense_tracker.model.Budget;
import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.Income;
import Nathanael.expense_tracker.model.SavingsGoal;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.UserRepository;
import Nathanael.expense_tracker.service.*;

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

@Controller
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserRepository userRepository;
    private final BudgetService budgetService;
    private final SavingsGoalService savingsGoalService;
    private final AccountService accountService;
    private final IncomeService incomeService;
    private final MonthlyTargetService monthlyTargetService;
    private final TransferService transferService;

    public ExpenseController(
            ExpenseService expenseService,
            UserRepository userRepository,
            BudgetService budgetService,
            SavingsGoalService savingsGoalService,
            AccountService accountService,
            IncomeService incomeService,
            MonthlyTargetService monthlyTargetService,
            TransferService transferService) {

        this.expenseService = expenseService;
        this.userRepository = userRepository;
        this.budgetService = budgetService;
        this.savingsGoalService = savingsGoalService;
        this.accountService = accountService;
        this.incomeService = incomeService;
        this.monthlyTargetService = monthlyTargetService;
        this.transferService = transferService;
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Map<String, String> getCategoryIcons() {
        Map<String, String> icons = new LinkedHashMap<>();
        icons.put("Food & Groceries", "🛒"); icons.put("Utilities", "🔧"); icons.put("Shopping", "🛍️"); icons.put("Housing", "🏠");
        icons.put("Transportation", "🚌"); icons.put("Healthcare", "🏥"); icons.put("Entertainment", "🎬"); icons.put("Career Development", "📈");
        icons.put("Travel", "✈️"); icons.put("Charity & Gifts", "🎁"); icons.put("Subscription", "🔁"); icons.put("Personal Care", "💇");
        icons.put("Pet", "🐾"); icons.put("Household Supplies", "🧴"); icons.put("Other", "📦");
        return icons;
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) return "\"" + value.replace("\"", "\"\"") + "\"";
        return value;
    }

    // --- EXPENSE CRUD ---
    @PostMapping("/add")
    public String addExpense(@Valid @ModelAttribute Expense expense, BindingResult result, Model model, Authentication authentication, @RequestParam(required = false) MultipartFile receipt) throws IOException {
        User currentUser = getCurrentUser(authentication);
        if (result.hasErrors()) return "redirect:/?tab=expenses";
        expense.setUser(currentUser);
        if (expense.isRecurring()) expense.setNextDueDate(expense.getDate());
        if (receipt != null && !receipt.isEmpty()) {
            expense.setReceiptImage(receipt.getBytes());
            expense.setReceiptContentType(receipt.getContentType());
        }
        expenseService.addExpense(expense);
        return "redirect:/?tab=expenses";
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Expense expense = expenseService.getExpenseById(id);
        if (expense != null && expense.getUser().getId().equals(currentUser.getId())) {
            expenseService.deleteExpense(id);
        }
        return "redirect:/?tab=expenses";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Expense expense = expenseService.getExpenseById(id);
        if (expense == null || !expense.getUser().getId().equals(currentUser.getId())) return "redirect:/?tab=expenses";
        model.addAttribute("expense", expense);
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateExpense(@PathVariable Long id, @ModelAttribute Expense expense, Authentication authentication, @RequestParam(required = false) MultipartFile receipt) throws IOException {
        User currentUser = getCurrentUser(authentication);
        Expense existing = expenseService.getExpenseById(id);
        if (existing != null && existing.getUser().getId().equals(currentUser.getId())) {
            existing.setDescription(expense.getDescription());
            existing.setAmount(expense.getAmount());
            existing.setCategory(expense.getCategory());
            existing.setDate(expense.getDate());
            existing.setRecurring(expense.isRecurring());
            existing.setRecurrenceFrequency(expense.getRecurrenceFrequency());
            if (expense.isRecurring()) existing.setNextDueDate(expense.getDate());
            else existing.setNextDueDate(null);
            if (receipt != null && !receipt.isEmpty()) {
                existing.setReceiptImage(receipt.getBytes());
                existing.setReceiptContentType(receipt.getContentType());
            }
            expenseService.addExpense(existing);
        }
        return "redirect:/?tab=expenses";
    }

    // --- INCOME CRUD ---
    @PostMapping("/income")
    public String addIncome(@RequestParam String source, @RequestParam double amount, @RequestParam String date, Authentication authentication) {
        incomeService.addIncome(new Income(source, amount, LocalDate.parse(date), getCurrentUser(authentication)));
        return "redirect:/?tab=income";
    }

    @GetMapping("/income/{id}/delete")
    public String deleteIncome(@PathVariable Long id, Authentication authentication) {
        incomeService.deleteIncome(id, getCurrentUser(authentication));
        return "redirect:/?tab=income";
    }

    @PostMapping("/income/update/{id}")
    public String updateIncome(@PathVariable Long id, @RequestParam String source, @RequestParam double amount, @RequestParam String date, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Income existing = incomeService.getIncomeById(id);
        if (existing != null && existing.getUser().getId().equals(currentUser.getId())) {
            existing.setSource(source);
            existing.setAmount(amount);
            existing.setDate(LocalDate.parse(date));
            incomeService.addIncome(existing);
        }
        return "redirect:/?tab=income";
    }

    // --- TRANSFERS ---
    @PostMapping("/transfers")
    public String createTransfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam Double amount, @RequestParam String date, @RequestParam(required = false) String description, Authentication authentication) {
        transferService.createTransfer(getCurrentUser(authentication), fromAccountId, toAccountId, amount, LocalDate.parse(date), description);
        return "redirect:/?tab=transfers";
    }

    // --- BUDGETING & GOALS ---
    @PostMapping("/budget")
    public String saveBudget(@RequestParam String category, @RequestParam double limitAmount, Authentication authentication) {
        budgetService.saveBudget(getCurrentUser(authentication), category, limitAmount);
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/budget/{id}/edit")
    public String editBudget(@PathVariable Long id, @RequestParam String category, @RequestParam double limitAmount, Authentication authentication) {
        if (category != null && !category.isBlank() && Double.isFinite(limitAmount) && limitAmount > 0) {
            budgetService.updateBudget(id, category, limitAmount, getCurrentUser(authentication));
        }
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/budget/{id}/delete")
    public String deleteBudget(@PathVariable Long id, Authentication authentication) {
        budgetService.deleteBudget(id, getCurrentUser(authentication));
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/goals")
    public String createGoal(@RequestParam String name, @RequestParam double targetAmount, @RequestParam(required = false) String targetDate, @RequestParam(required = false) String category, Authentication authentication) {
        LocalDate parsedDate = (targetDate != null && !targetDate.isBlank()) ? LocalDate.parse(targetDate) : null;
        String parsedCategory = (category != null && !category.isBlank()) ? category : null;
        savingsGoalService.createGoal(new SavingsGoal(name, targetAmount, parsedDate, parsedCategory, getCurrentUser(authentication)));
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/goals/{id}/edit")
    public String editGoal(@PathVariable Long id, @RequestParam String name, @RequestParam double targetAmount, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        savingsGoalService.updateGoal(id, name, targetAmount, currentUser);
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/goals/{id}/contribute")
    public String contributeToGoal(@PathVariable Long id, @RequestParam double amount, Authentication authentication) {
        savingsGoalService.addContribution(id, amount, getCurrentUser(authentication));
        return "redirect:/?tab=accounts";
    }

    @GetMapping("/goals/{id}/delete")
    public String deleteGoal(@PathVariable Long id, Authentication authentication) {
        savingsGoalService.deleteGoal(id, getCurrentUser(authentication));
        return "redirect:/?tab=accounts";
    }

    // --- ACCOUNTS ---
    @PostMapping("/accounts")
    public String createAccount(@RequestParam String name, @RequestParam String type, @RequestParam double balance, Authentication authentication) {
        accountService.addAccount(new Account(name, type, balance, getCurrentUser(authentication)));
        return "redirect:/?tab=accounts";
    }

    @PostMapping("/accounts/{id}/edit")
    public String editAccount(@PathVariable Long id, @RequestParam String name, @RequestParam String type, @RequestParam double balance, Authentication authentication) {
        accountService.updateAccount(id, name, type, balance, getCurrentUser(authentication));
        return "redirect:/?tab=accounts";
    }

    @GetMapping("/accounts/{id}/delete")
    public String deleteAccount(@PathVariable Long id, Authentication authentication) {
        accountService.deleteAccount(id, getCurrentUser(authentication));
        return "redirect:/?tab=accounts";
    }

    // --- MONTHLY TARGET ---
    @PostMapping("/target")
    public String saveTarget(@RequestParam double targetSurplus, Authentication authentication) {
        monthlyTargetService.saveTarget(getCurrentUser(authentication), targetSurplus);
        return "redirect:/?tab=monthly";
    }

    // --- EXPORTING ---
    @GetMapping("/export/csv")
    public void exportCsv(Authentication authentication, HttpServletResponse response) throws IOException {
        var expenses = expenseService.getExpensesForUser(getCurrentUser(authentication));
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.csv");
        PrintWriter writer = response.getWriter();
        writer.println("Description,Amount,Category,Date");
        for (Expense e : expenses) writer.println(escapeCsv(e.getDescription()) + "," + e.getAmount() + "," + escapeCsv(e.getCategory()) + "," + e.getDate());
        writer.flush();
    }

    @GetMapping("/export/pdf")
    public void exportPdf(Authentication authentication, HttpServletResponse response) throws IOException {
        var expenses = expenseService.getExpensesForUser(getCurrentUser(authentication));
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            document.add(new Paragraph("Expense Report - " + getCurrentUser(authentication).getUsername(), new Font(Font.HELVETICA, 18, Font.BOLD)));
            double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
            document.add(new Paragraph("Total expenses: " + expenses.size() + " | Total spent: ₦" + String.format("%,.2f", total)));
            PdfPTable table = new PdfPTable(4); table.setWidthPercentage(100);
            String[] headers = {"Description", "Amount", "Category", "Date"};
            for (String h : headers) { PdfPCell cell = new PdfPCell(new Phrase(h)); cell.setBackgroundColor(new Color(79, 70, 229)); table.addCell(cell); }
            for (Expense e : expenses) { table.addCell(e.getDescription()); table.addCell("₦" + e.getAmount()); table.addCell(e.getCategory()); table.addCell(e.getDate().toString()); }
            document.add(table);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } finally {
            if (document.isOpen()) document.close();
        }
    }

    @GetMapping("/receipt/{id}")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id, Authentication authentication) {
        Expense expense = expenseService.getExpenseById(id);
        if (expense == null || !expense.getUser().getId().equals(getCurrentUser(authentication).getId()) || expense.getReceiptImage() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(expense.getReceiptContentType())).body(expense.getReceiptImage());
    }

    // --- MAIN DASHBOARD ---
    @GetMapping("/")
    public String home(Model model, @RequestParam(required = false) String search, @RequestParam(required = false) String category, @RequestParam(required = false, defaultValue = "accounts") String tab, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        var expenses = expenseService.getExpensesForUser(currentUser);

        if (expenses == null) expenses = List.of();

        if (search != null && !search.isBlank()) {
            expenses = expenses.stream().filter(e -> e.getDescription() != null && e.getDescription().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
        }
        if (category != null && !category.isBlank()) {
            expenses = expenses.stream().filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category)).collect(Collectors.toList());
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();
        var categoryTotals = expenses.stream().collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        Map<String, Double> monthlyTotalsRaw = expenses.stream().collect(Collectors.groupingBy(e -> e.getDate().format(formatter), Collectors.summingDouble(Expense::getAmount)));
        Map<String, Double> monthlyTotals = expenses.stream().map(e -> e.getDate().withDayOfMonth(1)).distinct().sorted((a, b) -> b.compareTo(a)).collect(Collectors.toMap(d -> d.format(formatter), d -> monthlyTotalsRaw.getOrDefault(d.format(formatter), 0.0), (a, b) -> a, LinkedHashMap::new));

        List<Budget> budgets = budgetService.getBudgetsForUser(currentUser);
        if (budgets == null) budgets = List.of();

        var budgetProgress = budgets.stream().collect(Collectors.toMap(Budget::getCategory, b -> {
            double spent = categoryTotals.getOrDefault(b.getCategory(), 0.0);
            return new double[]{spent, b.getLimitAmount(), b.getLimitAmount() > 0 ? Math.min((spent / b.getLimitAmount()) * 100, 100) : 0};
        }, (existing, replacement) -> replacement, LinkedHashMap::new));

        LocalDate currentMonthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate previousMonthStart = currentMonthStart.minusMonths(1);
        Map<String, Double> currentMonthTotals = expenses.stream().filter(e -> !e.getDate().isBefore(currentMonthStart)).collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
        Map<String, Double> previousMonthTotals = expenses.stream().filter(e -> !e.getDate().isBefore(previousMonthStart) && e.getDate().isBefore(currentMonthStart)).collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));
        Map<String, double[]> monthComparison = new LinkedHashMap<>();

        for (String cat : currentMonthTotals.keySet()) {
            double current = currentMonthTotals.getOrDefault(cat, 0.0);
            double previous = previousMonthTotals.getOrDefault(cat, 0.0);
            monthComparison.put(cat, new double[]{current, previous, previous > 0 ? ((current - previous) / previous) * 100 : (current > 0 ? 100 : 0)});
        }

        List<Income> incomeList = incomeService.getIncomeForUser(currentUser);
        if (incomeList == null) incomeList = List.of();

        double currentMonthExpenseTotal = currentMonthTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        double currentMonthIncomeTotal = incomeList.stream().filter(inc -> !inc.getDate().isBefore(currentMonthStart)).mapToDouble(Income::getAmount).sum();
        double netSurplus = currentMonthIncomeTotal - currentMonthExpenseTotal;
        double targetSurplus = monthlyTargetService.getTargetForUser(currentUser);

        model.addAttribute("tab", tab);
        model.addAttribute("currentMonthIncomeTotal", currentMonthIncomeTotal);
        model.addAttribute("currentMonthExpenseTotal", currentMonthExpenseTotal);
        model.addAttribute("netSurplus", netSurplus);
        model.addAttribute("targetSurplus", targetSurplus);
        model.addAttribute("targetGap", targetSurplus - netSurplus);
        model.addAttribute("currentMonthLabel", currentMonthStart.format(formatter));
        model.addAttribute("incomeList", incomeList);
        model.addAttribute("totalIncome", incomeList.stream().mapToDouble(Income::getAmount).sum());
        model.addAttribute("accounts", accountService.getAccountsForUser(currentUser));
        model.addAttribute("savingsGoals", savingsGoalService.getGoalsForUser(currentUser));
        model.addAttribute("monthComparison", monthComparison);
        model.addAttribute("expenses", expenses);
        model.addAttribute("total", total);
        model.addAttribute("count", expenses.size());
        model.addAttribute("average", expenses.isEmpty() ? 0 : total / expenses.size());
        model.addAttribute("categoryTotals", categoryTotals);
        model.addAttribute("categoryIcons", getCategoryIcons());
        model.addAttribute("monthlyTotals", monthlyTotals);
        model.addAttribute("budgets", budgets);
        model.addAttribute("budgetProgress", budgetProgress);
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("search", search);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("bills", expenseService.getBillsForUser(currentUser));

        model.addAttribute("transfers", transferService.getTransfersForUser(currentUser));

        return "index";
    }
}