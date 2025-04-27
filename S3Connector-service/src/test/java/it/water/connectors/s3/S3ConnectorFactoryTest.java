package it.water.connectors.s3;

import com.adobe.testing.s3mock.junit5.S3MockExtension;
import it.water.connectors.s3.model.S3ClientConfig;
import it.water.connectors.s3.service.S3ConnectorFactory;
import it.water.connectors.s3.service.S3ConnectorSystemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

class S3ConnectorFactoryTest {

    @RegisterExtension
    static final S3MockExtension s3Mock = S3MockExtension.builder()
            .withSecureConnection(false)
            .build();

    @Test
    void testPrivateConstructor() throws Exception {
        Constructor<S3ConnectorFactory> constructor = S3ConnectorFactory.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        S3ConnectorFactory factory = constructor.newInstance();
        assertNotNull(factory);
    }

    @Test
    void testConnect() {
        S3ClientConfig config = new S3ClientConfig();
        config = S3ClientConfig.builder()
                .accessKey("dummy")
                .secretKey("dummy")
                .region("eu-west-1")
                .bucketName("mock-bucket")
                .endpoint(s3Mock.getServiceEndpoint().toString()) // <<< usa il mock
                .build();
        S3ConnectorSystemServiceImpl service = S3ConnectorFactory.connect(config);

        assertNotNull(service);

    }

    @Test
    void configureShouldHandleNullEndpoint() {
        S3ClientConfig config = S3ClientConfig.builder()
                .accessKey("dummy")
                .secretKey("dummy")
                .region("eu-west-1")
                .bucketName("mock-bucket")
                .endpoint(null)  // <<< Qui Endpoint è null
                .build();

        S3ConnectorSystemServiceImpl service = S3ConnectorFactory.connect(config);

        assertNotNull(service);
    }
}
