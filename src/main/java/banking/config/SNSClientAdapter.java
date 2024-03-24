package banking.config;

import io.netty.handler.ssl.SslProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;

/**
 * SNSClientAdapter provides the default async HTTP client implementation along with OpenSSL as the SSL provider.
 */
@Component("clientAdapter")
@ConditionalOnProperty(value = "deploy.snsAdapter", havingValue = "sns")
public class SNSClientAdapter implements ISnsClientAdapter {
    private final SdkAsyncHttpClient client;

    public SNSClientAdapter() {
        // Use OpenSSL for the SSL provider: this has been shown to deliver better performance.
        // See: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/best-practices.html
        this.client = NettyNioAsyncHttpClient.builder()
                .sslProvider(SslProvider.OPENSSL)
                .build();
    }

    @Override
    public SdkAsyncHttpClient getClient() {
        return client;
    }
}
