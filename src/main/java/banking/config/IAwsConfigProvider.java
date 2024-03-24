package banking.config;

import software.amazon.awssdk.regions.Region;

/**
 * IAwsConfigProvider is used to provide AWS configuration to services.
 */
public interface IAwsConfigProvider {
    Long apiCallTimeout();

    Long apiCallAttemptTimeout();

    String arn();

    Region region();
}
