package banking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

/**
 * MockCredentials loads fake credentials from configuration. Not intended for production use.
 */
@Component("credentialsProvider")
public class MockCredentials implements AwsCredentialsProvider {
    private final String accessKeyId;
    private final String secretAccessKey;

    public MockCredentials(@Value("${aws.accessKeyId}") String accessKeyId, @Value("${aws.secretAccessKey}") String secretAccessKey) {
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }

    @Override
    public AwsCredentials resolveCredentials() {
        return AwsBasicCredentials.create(this.accessKeyId, this.secretAccessKey);
    }
}
