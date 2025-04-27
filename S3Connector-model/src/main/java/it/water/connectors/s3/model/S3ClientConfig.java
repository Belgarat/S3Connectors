package it.water.connectors.s3.model;

import lombok.*;

/**
 * Configuration model for connecting to an S3 bucket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3ClientConfig {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
    private String endpoint;

    public void validate() {
        if (isNullOrEmpty(accessKey)) {
            throw new IllegalArgumentException("AccessKey must not be null or empty");
        }
        if (isNullOrEmpty(secretKey)) {
            throw new IllegalArgumentException("SecretKey must not be null or empty");
        }
        if (isNullOrEmpty(region)) {
            throw new IllegalArgumentException("Region must not be null or empty");
        }
        if (isNullOrEmpty(bucketName)) {
            throw new IllegalArgumentException("BucketName must not be null or empty");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
