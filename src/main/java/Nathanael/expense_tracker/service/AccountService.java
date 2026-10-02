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

    public Account addAccount(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> getAccountsForUser(User user) {
        return accountRepository.findByUser(user);
    }

    public void deleteAccount(Long id, User user) {

        Account account = accountRepository.findById(id)
                .orElse(null);

        if (account != null
                && account.getUser().getId().equals(user.getId())) {

            accountRepository.delete(account);
        }
    }
}