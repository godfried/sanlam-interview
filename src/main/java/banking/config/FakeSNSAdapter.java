package banking.config;

import org.http4k.aws.AwsSdkAsyncClient;
import org.http4k.connect.amazon.sns.FakeSNS;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;

/**
 * FakeSNSAdapter provides a mock async SNS HTTP client which can be used instead of the
 * real thing when testing.
 */
@Component("clientAdapter")
@ConditionalOnProperty(value = "deploy.snsAdapter", havingValue = "fake")
public class FakeSNSAdapter implements ISnsClientAdapter {

    private final AwsSdkAsyncClient client;

    public FakeSNSAdapter() {
        FakeSNS fakeSNS = new FakeSNS();
        this.client = new AwsSdkAsyncClient(fakeSNS);
    }


    @Override
    public SdkAsyncHttpClient getClient() {
        return client;
    }
}
