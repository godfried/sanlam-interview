package banking.service;

import java.math.BigDecimal;

public interface ISnsService {
    void publishWithdrawal(Long accountId, BigDecimal amount);
}
