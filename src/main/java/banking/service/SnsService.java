package banking.service;

import banking.config.IAwsConfigProvider;
import banking.config.ISnsClientAdapter;
import banking.dto.WithdrawalEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsAsyncClient;
import software.amazon.awssdk.services.sns.model.PublishBatchRequest;
import software.amazon.awssdk.services.sns.model.PublishBatchRequestEntry;
import software.amazon.awssdk.services.sns.model.PublishBatchResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class SnsService implements ISnsService {
    private static final int SNS_BATCH_SIZE = 10;
    private static Logger logger = LogManager.getLogger(AccountService.class);
    private final SnsAsyncClient snsClient;
    // Move AWS config to an external source for easier testing and improved portability.
    private final IAwsConfigProvider arnProvider;
    private final ObjectMapper objectMapper;
    private final LinkedBlockingQueue<PublishBatchRequestEntry> messageQueue;

    public SnsService(@Autowired ISnsClientAdapter clientAdapter, @Autowired IAwsConfigProvider arnProvider, @Autowired AwsCredentialsProvider credentialsProvider) {
        this.arnProvider = arnProvider;
        // Prevent a bottleneck from forming around SNS calls by making them async.
        this.snsClient = SnsAsyncClient.builder()
                .httpClient(clientAdapter.getClient())
                .region(this.arnProvider.region())
                .credentialsProvider(credentialsProvider)
                .build();
        this.objectMapper = new ObjectMapper();
        this.messageQueue = new LinkedBlockingQueue<PublishBatchRequestEntry>(100);
    }

    public void publishWithdrawal(Long accountId, BigDecimal amount) {
        // After a successful withdrawal, publish a withdrawal event to SNS
        WithdrawalEvent event = new WithdrawalEvent(amount, accountId, "SUCCESSFUL");
        String message = null;
        try {
            // Use jackson for JSON serialization for improved performance.
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.error(new StringMapMessage().
                    with("context", "sns withdrawal publish").
                    with("error", e.getMessage()).
                    with("accountId", accountId).
                    with("amount", amount.toString()));
            return;
        }
        // Use batch calls to limit API calls and bring down costs
        PublishBatchRequestEntry entry = PublishBatchRequestEntry.
                builder().
                message(message).
                build();
        messageQueue.add(entry);
        logger.debug(new StringMapMessage().
                with("context", "sns publish: event queued").
                with("event", event));
        while (messageQueue.size() >= SNS_BATCH_SIZE) {
            List<PublishBatchRequestEntry> drained = new ArrayList<>(SNS_BATCH_SIZE);
            messageQueue.drainTo(drained, SNS_BATCH_SIZE);
            PublishBatchRequest publishRequest = PublishBatchRequest.builder()
                    .publishBatchRequestEntries(drained)
                    .topicArn(this.arnProvider.arn())
                    .build();
            CompletableFuture<PublishBatchResponse> publishResponse = snsClient.publishBatch(publishRequest);
            publishResponse.thenAccept(response -> logger.debug(new StringMapMessage().
                    with("context", "sns publish batch response succeeded").
                    with("response", response)));
        }
    }
}
