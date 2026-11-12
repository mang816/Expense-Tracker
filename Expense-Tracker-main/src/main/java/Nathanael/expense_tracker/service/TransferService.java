package Nathanael.expense_tracker.service;

import Nathanael.expense_tracker.model.Account;
import Nathanael.expense_tracker.model.Transfer;
import Nathanael.expense_tracker.model.User;
import Nathanael.expense_tracker.repository.AccountRepository;
import Nathanael.expense_tracker.repository.TransferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transfer> getTransfersForUser(User user) {
        return transferRepository.findByUserOrderByDateDesc(user);
    }

    @Transactional
    public void createTransfer(User user, Long fromAccountId, Long toAccountId, Double amount, LocalDate date, String description) {
        Account from = accountRepository.findById(fromAccountId).orElseThrow();
        Account to = accountRepository.findById(toAccountId).orElseThrow();

        if (!from.getUser().getId().equals(user.getId()) || !to.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        accountRepository.save(from);
        accountRepository.save(to);

        Transfer transfer = new Transfer();
        transfer.setUser(user);
        transfer.setFromAccount(from);
        transfer.setToAccount(to);
        transfer.setAmount(amount);
        transfer.setDate(date);
        transfer.setDescription(description);

        transferRepository.save(transfer);
    }
}