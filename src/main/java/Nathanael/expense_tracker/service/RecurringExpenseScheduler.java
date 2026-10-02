package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.repository.ExpenseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecurringExpenseScheduler {

    private final ExpenseRepository expenseRepository;

    public RecurringExpenseScheduler(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Scheduled(cron = "0 0 1 * * *") // runs every day at 1:00 AM
    public void generateDueRecurringExpenses() {

        LocalDate today = LocalDate.now();

        List<Expense> dueExpenses = expenseRepository
                .findByRecurringTrueAndNextDueDateLessThanEqual(today);

        for (Expense original : dueExpenses) {

            Expense newExpense = new Expense(
                    original.getDescription(),
                    original.getAmount(),
                    original.getCategory(),
                    original.getNextDueDate()
            );

            newExpense.setUser(original.getUser());
            newExpense.setRecurring(false); // the auto-created copy is a one-off, not itself recurring

            expenseRepository.save(newExpense);

            // Push the original's next due date forward
            if ("WEEKLY".equals(original.getRecurrenceFrequency())) {
                original.setNextDueDate(original.getNextDueDate().plusWeeks(1));
            } else if ("MONTHLY".equals(original.getRecurrenceFrequency())) {
                original.setNextDueDate(original.getNextDueDate().plusMonths(1));
            }

            expenseRepository.save(original);
        }
    }
}