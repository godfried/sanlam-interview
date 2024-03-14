package me.jordaan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
@RestController
@RequestMapping("/bank")
public class BankAccountController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SnsClient snsClient;
    public BankAccountController() {
        this.snsClient = SnsClient.builder()
                .region(Region.AF_SOUTH_1) // Specify your region
                .build();
    }
    @PostMapping("/withdraw")
    public String withdraw(@RequestParam("accountId") Long accountId,
                           @RequestParam("amount") BigDecimal amount) {
// Check current balance
        String sql = "SELECT balance FROM accounts WHERE id = ?";
        BigDecimal currentBalance = jdbcTemplate.queryForObject(
                sql, new Object[]{accountId}, BigDecimal.class);
        if (currentBalance != null && currentBalance.compareTo(amount) >= 0) {
// Update balance
            sql = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
            int rowsAffected = jdbcTemplate.update(sql, amount, accountId);
            if (rowsAffected > 0) {
                publishWithdrawal(accountId, amount);
                return "Withdrawal successful";
            } else {
// In case the update fails for reasons other than a balance check
                return "Withdrawal failed";
            }
        } else {
// Insufficient funds
            return "Insufficient funds for withdrawal";
        }

    }

    public void publishWithdrawal(Long accountId, BigDecimal amount){
        // After a successful withdrawal, publish a withdrawal event to SNS
        WithdrawalEvent event = new WithdrawalEvent(amount, accountId, "SUCCESSFUL");
        String eventJson = event.toJson(); // Convert event to JSON
        String snsTopicArn = "arn:aws:sns:YOUR_REGION:YOUR_ACCOUNT_ID:YOUR_TOPIC_NAME";
        PublishRequest publishRequest = PublishRequest.builder()
                .message(eventJson)
                .topicArn(snsTopicArn)
                .build();
        PublishResponse publishResponse = snsClient.publish(publishRequest);
    }
}
