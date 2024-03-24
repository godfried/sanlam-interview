package banking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Component("arnProvider")
public class AwsConfigProvider implements IAwsConfigProvider {

    private final String accountId;
    private final String withdrawalTopic;
    private final Region region;

    public AwsConfigProvider(@Value("aws.accountID") String accountID, @Value("aws.withdrawalTopic") String withdrawalTopic, @Value("aws.region") Region region) {
        this.accountId = accountID;
        this.withdrawalTopic = withdrawalTopic;
        this.region = region;
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
