package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Income;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.IncomeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {

    private final IncomeRepository incomeRepository;

    public IncomeService(IncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    public List<Income> getIncomeForUser(User user) {
        return incomeRepository.findByUser(user);
    }

    public void addIncome(Income income) {
        incomeRepository.save(income);
    }

    public void deleteIncome(Long id, User user) {

        Income income = incomeRepository.findById(id).orElse(null);

        if (income != null && income.getUser().getId().equals(user.getId())) {
            incomeRepository.deleteById(id);
        }
    }
}