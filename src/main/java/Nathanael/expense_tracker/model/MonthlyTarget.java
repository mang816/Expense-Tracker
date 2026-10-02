package Nathanael.expense_tracker.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class MonthlyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double targetSurplus;

    @OneToOne
    private User user;

    public MonthlyTarget() {
    }

    public MonthlyTarget(double targetSurplus, User user) {
        this.targetSurplus = targetSurplus;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public double getTargetSurplus() {
        return targetSurplus;
    }

    public void setTargetSurplus(double targetSurplus) {
        this.targetSurplus = targetSurplus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}