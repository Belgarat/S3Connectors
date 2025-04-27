package it.water.connectors.s3;

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import it.water.connectors.s3.model.S3ClientConfig;
import it.water.connectors.s3.service.S3ConnectorFactory;
import it.water.connectors.s3.service.S3ConnectorSystemServiceImpl;
import it.water.core.api.service.Service;
import it.water.core.testing.utils.junit.WaterTestExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3ConnectorApiTest implements Service {

    private S3ConnectorSystemServiceImpl service;
    private S3Client mockS3Client;

    @RegisterExtension
    static final S3MockExtension s3Mock = S3MockExtension.builder()
            .withSecureConnection(false)
            .build();

    @BeforeEach
    void setup() {
        mockS3Client = mock(S3Client.class);

        var config = S3ClientConfig.builder()
                .accessKey("dummy")
                .secretKey("dummy")
                .region("eu-west-1")
                .bucketName("mock-bucket")
                .endpoint(s3Mock.getServiceEndpoint().toString()) // <<< usa il mock
                .build();

        service = S3ConnectorFactory.connect(config);
        service.setS3Client(mockS3Client);
    }

    @Test
    void configureShouldHandleNullEndpoint() {
        // Creo una configurazione senza endpoint per testare il ramo if
        var configWithoutEndpoint = S3ClientConfig.builder()
                .accessKey("dummy")
                .secretKey("dummy")
                .region("eu-west-1")
                .bucketName("mock-bucket")
                .endpoint(null)
                .build();

        var serviceWithoutEndpoint = S3ConnectorFactory.connect(configWithoutEndpoint);
        serviceWithoutEndpoint.setS3Client(mock(S3Client.class));

        // Ora puoi eseguire test sul service configurato senza endpoint
        assertNotNull(serviceWithoutEndpoint);
    }

    @Test
    void listObjectsReturnsExpectedKeys() {
        ListObjectsV2Response mockResponse = ListObjectsV2Response.builder()
                .contents(
                        S3Object.builder().key("file1.txt").build(),
                        S3Object.builder().key("folder/file2.txt").build()
                )
                .build();

        when(mockS3Client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(mockResponse);

        List<String> result = service.listObjects("folder/");
        assertEquals(List.of("file1.txt", "folder/file2.txt"), result);
    }

    @Test
    void getMetadataReturnsExpectedValues() {
        HeadObjectResponse mockResponse = HeadObjectResponse.builder()
                .metadata(java.util.Map.of("author", "test-user"))
                .build();

        when(mockS3Client.headObject(any(HeadObjectRequest.class))).thenReturn(mockResponse);

        var result = service.getMetadata("some-file.txt");
        assertEquals("test-user", result.get("author"));
    }

    @Test
    void uploadFileCallsPutObject() {
        byte[] content = "hello".getBytes();

        service.uploadFile("file.txt", content, java.util.Map.of("type", "text"));

        verify(mockS3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void createDirectoryAddsSlashIfMissing() {
        service.createDirectory("folder");

        verify(mockS3Client).putObject(
                argThat((PutObjectRequest req) -> req.key().equals("folder/")),
                any(RequestBody.class)
        );
    }

    @Test
    void createDirectoryDoesNotAddSlashIfAlreadyPresent() {
        service.createDirectory("folder/");

        verify(mockS3Client).putObject(
                argThat((PutObjectRequest req) -> req.key().equals("folder/")),
                any(RequestBody.class)
        );
    }



    @Test
    void deleteFileCallsDeleteObject() {
        service.delete("file.txt");
        verify(mockS3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
