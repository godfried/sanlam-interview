package banking.beans;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.http.ExecutableHttpRequest;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.SdkHttpClient;

@Component("clientAdapter")
public class MockSNSAdapter implements SdkHttpClient {

    @Override
    public ExecutableHttpRequest prepareRequest(HttpExecuteRequest httpExecuteRequest) {
        return null;
    }

    @Override
    public void close() {

    }
}
