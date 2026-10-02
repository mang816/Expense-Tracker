package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.MonthlyTarget;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.MonthlyTargetRepository;
import org.springframework.stereotype.Service;

@Service
public class MonthlyTargetService {

    private final MonthlyTargetRepository monthlyTargetRepository;

    public MonthlyTargetService(MonthlyTargetRepository monthlyTargetRepository) {
        this.monthlyTargetRepository = monthlyTargetRepository;
    }

    public double getTargetForUser(User user) {
        return monthlyTargetRepository.findByUser(user)
                .map(MonthlyTarget::getTargetSurplus)
                .orElse(0.0);
    }

    public void saveTarget(User user, double targetSurplus) {

        MonthlyTarget target = monthlyTargetRepository.findByUser(user)
                .orElse(new MonthlyTarget(targetSurplus, user));

        target.setTargetSurplus(targetSurplus);

        monthlyTargetRepository.save(target);
    }
}