package banking.config;

import software.amazon.awssdk.regions.Region;

/**
 * AwsArnProvider is used to provide AWS configuration to services.
 */
public interface IAwsConfigProvider {
    String arn();

    Region region();
}
