package it.water.connectors.s3.service;

import it.water.connectors.s3.api.S3ConnectorSystemApi;
import it.water.connectors.s3.model.S3ClientConfig;
import it.water.core.api.interceptors.OnActivate;
import it.water.core.api.registry.ComponentRegistry;
import it.water.core.api.security.EncryptionUtil;
import it.water.core.interceptors.annotations.*;

import it.water.core.service.BaseSystemServiceImpl;

import lombok.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @Generated by Water Generator
 * System Service Api Class for S3Connector entity.
 *
 */
@Setter
@FrameworkComponent
public class S3ConnectorSystemServiceImpl extends BaseSystemServiceImpl implements S3ConnectorSystemApi {
     // e' valido in tutto tranne che nell'OnActivate
    @Inject
    private ComponentRegistry componentRegistry;

    @Inject
    private EncryptionUtil encryptionUtil;

    @Inject
    private Runtime runtime;

    private S3Client s3Client;

    private S3ClientConfig config;

    @OnActivate
    public void init(ComponentRegistry componentRegistry) {
        getLog().info("INIT ******************** ");
    }


    public void configure(S3ClientConfig config) {

        AwsBasicCredentials creds = AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey());

        S3ClientBuilder builder = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(creds))
                .region(Region.of(config.getRegion()));

        if (config.getEndpoint() != null && !config.getEndpoint().isEmpty()) {
            builder.endpointOverride(URI.create(config.getEndpoint()));
        }
        this.config = config;
        this.s3Client = builder.build();
    }


    @Override
    public List<String> listObjects(String prefix) {
        ListObjectsV2Response res = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(this.config.getBucketName())
                .prefix(prefix)
                .build());

        return res.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, String> getMetadata(String path) {
        HeadObjectResponse res = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(this.config.getBucketName())
                .key(path)
                .build());

        return res.metadata();
    }

    @Override
    public void uploadFile(String path, byte[] content, Map<String, String> metadata) {
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(this.config.getBucketName())
                        .key(path)
                        .metadata(metadata)
                        .build(),
                RequestBody.fromBytes(content));
    }

    @Override
    public void createDirectory(String path) {
        // S3 non ha directory reali, si crea un oggetto fittizio che termina con /
        if (!path.endsWith("/")) {
            path += "/";
        }
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(this.config.getBucketName())
                        .key(path)
                        .build(),
                RequestBody.empty());
    }

    @Override
    public void delete(String path) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(this.config.getBucketName())
                .key(path)
                .build());
    }

    @Override
    public void update(String path, byte[] newContent, Map<String, String> newMetadata) {
        // In S3 la "modifica" equivale a fare overwrite
        uploadFile(path, newContent, newMetadata);
    }

}