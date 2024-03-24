package banking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Component("arnProvider")
public class AwsConfigProvider implements IAwsConfigProvider {

    private final String accountId;
    private final String withdrawalTopic;
    private final Region region;
    private final Long apiCallTimeout;
    private final Long apiCallAttemptTimeout;

    public AwsConfigProvider(@Value("${aws.accountID}") String accountID, @Value("${aws.withdrawalTopic}") String withdrawalTopic, @Value("aws.region") Region region, @Value("${aws.apiCallTimeout}") Long apiCallTimeout, @Value("${aws.apiCallAttemptTimeout}") Long apiCallAttemptTimeout) {
        this.accountId = accountID;
        this.withdrawalTopic = withdrawalTopic;
        this.region = region;
        this.apiCallTimeout = apiCallTimeout;
        this.apiCallAttemptTimeout = apiCallAttemptTimeout;
    }

    @Override
    public Long apiCallTimeout() {
        return this.apiCallTimeout;
    }

    @Override
    public Long apiCallAttemptTimeout() {
        return this.apiCallAttemptTimeout;
    }


    @Override
    public String arn() {
        return String.format("arn:aws:sns:%s:%s:%s", this.region.id(), this.accountId, this.withdrawalTopic);
    }

    @Override
    public Region region() {
        return this.region;
    }
}
