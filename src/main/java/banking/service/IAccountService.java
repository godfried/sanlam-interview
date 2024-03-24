package banking.service;

import banking.enums.WithdrawalResult;

import java.math.BigDecimal;

public interface IAccountService {
    WithdrawalResult withdraw(Long accountId, BigDecimal amount);

    Long add(BigDecimal balance);

    BigDecimal balance(Long accountId);
}
