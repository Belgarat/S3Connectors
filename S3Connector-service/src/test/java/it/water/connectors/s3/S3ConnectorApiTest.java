package it.water.connectors.s3;
import it.water.connectors.s3.model.S3ClientConfig;
import it.water.connectors.s3.service.S3ConnectorFactory;
import it.water.connectors.s3.service.S3ConnectorSystemServiceImpl;
import it.water.core.api.service.Service;
import it.water.core.testing.utils.junit.WaterTestExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Generated with Water Generator.
 * Test class for S3Connector Services.
 

 */
@ExtendWith(WaterTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class S3ConnectorApiTest implements Service {
    private S3ConnectorSystemServiceImpl service;
    private S3Client mockS3Client;

    @BeforeEach
    void setup() {
        mockS3Client = mock(S3Client.class);

        // Creo una dummy S3Configuration
        var config = S3ClientConfig.builder()
                .accessKey("dummy")
                .secretKey("dummy")
                .region("eu-west-1")
                .bucketName("mock-bucket")
                .endpoint("http://localhost")
                .build();

        service = S3ConnectorFactory.connect(config);

        // Sovrascrivo il vero s3Client con il mock
        service.setS3Client(mockS3Client);
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
    void deleteFileCallsDeleteObject() {
        service.delete("file.txt");
        verify(mockS3Client).deleteObject(any(DeleteObjectRequest.class));
    }
}
