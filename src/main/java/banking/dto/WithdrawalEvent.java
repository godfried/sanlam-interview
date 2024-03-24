package banking.dto;

import java.math.BigDecimal;

public record WithdrawalEvent(BigDecimal amount, Long accountId, String status) {
}
