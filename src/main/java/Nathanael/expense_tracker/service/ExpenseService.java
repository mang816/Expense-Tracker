package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Expense;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<Expense> getExpensesForUser(User user) {
        return expenseRepository.findByUser(user);
    }

    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElse(null);
    }

    public Expense updateExpense(Long id, Expense updatedExpense) {

        Expense existingExpense = expenseRepository
                .findById(id)
                .orElse(null);

        if (existingExpense != null) {

            existingExpense.setDescription(
                    updatedExpense.getDescription()
            );

            existingExpense.setAmount(
                    updatedExpense.getAmount()
            );

            existingExpense.setCategory(
                    updatedExpense.getCategory()
            );

            existingExpense.setDate(
                    updatedExpense.getDate()
            );

            return expenseRepository.save(existingExpense);
        }

        return null;
    }
}