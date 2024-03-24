package banking.config;

import software.amazon.awssdk.http.async.SdkAsyncHttpClient;

public interface ISnsClientAdapter {
    SdkAsyncHttpClient getClient();
}
