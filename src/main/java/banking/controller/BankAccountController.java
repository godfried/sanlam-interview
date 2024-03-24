package banking.controller;

import banking.config.IAwsConfigProvider;
import banking.enums.WithdrawalResult;
import banking.service.IAccountService;
import banking.service.ISnsService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import java.math.BigDecimal;

@RestController
@RequestMapping("/bank")
public class BankAccountController {
    private static Logger logger = LogManager.getLogger(BankAccountController.class);
    private final ISnsService snsService;
    private final IAccountService accountService;

    public BankAccountController(@Autowired IAccountService accountService, @Autowired ISnsService snsService, @Autowired AwsCredentialsProvider credentialsProvider, @Autowired IAwsConfigProvider arnProvider) {
        this.accountService = accountService;
        this.snsService = snsService;
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("accountId") Long accountId,
                           @RequestParam("amount") BigDecimal amount, HttpServletResponse response) {
        WithdrawalResult result = accountService.withdraw(accountId, amount);
        logger.debug(new StringMapMessage().
                with("context", "withdrawal request received").
                with("accountId", accountId).
                with("amount", amount.toString()));
        // Send an SNS event if withdrawal was successful, otherwise set the status code to the appropriate error code.
        if (result.equals(WithdrawalResult.SUCCESS)) {
            snsService.publishWithdrawal(accountId, amount);
        } else if (result.isClientError()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (result.isServerError()) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        logger.debug(new StringMapMessage().
                with("context", "withdrawal request completed").
                with("result", result).
                with("accountId", accountId).
                with("amount", amount.toString()));
        return result.toString();
    }

    // Just for testing
    @PostMapping("/add")
    public Long add(@RequestParam("balance") BigDecimal balance) {
        return accountService.add(balance);
    }

    // Just for testing
    @GetMapping("/balance")
    public BigDecimal balance(@RequestParam("accountId") Long accountId) {
        return accountService.balance(accountId);
    }
}
