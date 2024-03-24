package banking.service;

import banking.enums.WithdrawalResult;
import banking.model.Account;
import banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class AccountService implements IAccountService {

    final AccountRepository accountRepository;

    public AccountService(@Autowired AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public WithdrawalResult withdraw(Long accountId, BigDecimal amount) {
        switch (amount.compareTo(BigDecimal.ZERO)) {
            case -1:
                return WithdrawalResult.ERR_NEGATIVE_AMOUNT;
            case 0:
                return WithdrawalResult.ERR_ZERO_AMOUNT;
        }
        Optional<Account> queriedAccount = accountRepository.findById(accountId);
        if (queriedAccount.isEmpty()) {
            return WithdrawalResult.ERR_UNKNOWN_ACCOUNT;
        }
        Account account = queriedAccount.get();
        BigDecimal currentBalance = account.getBalance();
        // balance is not nullable so no reason for extra check.
        if (currentBalance.compareTo(amount) >= 0) {
            // Update balance
            account.withDraw(amount);
            accountRepository.save(account);
            return WithdrawalResult.SUCCESS;
        } else {
            return WithdrawalResult.ERR_INSUFFICIENT_FUNDS;
        }
    }

    @Override
    public Long add(BigDecimal balance) {
        Account account = new Account(balance);
        Account saved = accountRepository.saveAndFlush(account);
        return saved.getId();
    }

    @Override
    public BigDecimal balance(Long accountId) {
        Optional<Account> queriedAccount = accountRepository.findById(accountId);
        if (queriedAccount.isEmpty()) {
            throw new NoSuchElementException(String.format("No account with ID %d", accountId));
        }
        return queriedAccount.get().getBalance();
    }
}
