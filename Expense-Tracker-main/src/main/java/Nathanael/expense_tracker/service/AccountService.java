package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Account;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAccountsForUser(User user) {
        return accountRepository.findByUser(user);
    }

    public void addAccount(Account account) {
        accountRepository.save(account);
    }

    public void updateAccount(Long id, String name, String type, double balance, User user) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null && account.getUser().getId().equals(user.getId())) {
            account.setName(name);
            account.setType(type);
            account.setBalance(balance);
            accountRepository.save(account);
        }
    }

    public void deleteAccount(Long id, User user) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null && account.getUser().getId().equals(user.getId())) {
            accountRepository.delete(account);
        }
    }
}