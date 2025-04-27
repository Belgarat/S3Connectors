package it.water.connectors.s3.service;

import it.water.connectors.s3.model.S3ClientConfig;

/**
 * Factory class for creating instances of S3ConnectorsSystemServiceImpl.
 */
public class S3ConnectorFactory {

    private void S3ConnectorFactory() {
        // Prevent instantiation
    }

    /**
     * Creates and configures a new S3ConnectorsSystemServiceImpl instance.
     *
     * @param config the S3 configuration
     * @return a configured S3ConnectorsSystemServiceImpl
     */
    public static S3ConnectorSystemServiceImpl connect(S3ClientConfig config) {
        config.validate();
        S3ConnectorSystemServiceImpl instance = new S3ConnectorSystemServiceImpl();
        instance.configure(config);
        return instance;
    }
}
