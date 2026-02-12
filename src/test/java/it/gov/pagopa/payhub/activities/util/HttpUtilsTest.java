package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.config.rest.HttpClientConfig;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.function.Resolver;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.file.Path;

class HttpUtilsTest {

    @Test
    void whenGetPooledConnectionManagerBuilderThenReturnConfiguredConnectionManager() throws NoSuchFieldException, IllegalAccessException {
        // Given
        HttpClientConfig httpClientConfig = buildTestHttpClientConfig();
        TlsSocketStrategy tlsSocketStrategy = Mockito.mock(TlsSocketStrategy.class);

        // When
        PoolingHttpClientConnectionManager result = HttpUtils.getPooledConnectionManagerBuilder(httpClientConfig, tlsSocketStrategy).build();

        // Then
        assertHttpClientConnectionManager(result);
    }

    @Test
    void whenBuildPooledConnectionThenReturnConfiguredConnectionManager() throws NoSuchFieldException, IllegalAccessException {
        // Given
        HttpClientConfig httpClientConfig = buildTestHttpClientConfig();
        TlsSocketStrategy tlsSocketStrategy = Mockito.mock(TlsSocketStrategy.class);

        // When
        HttpComponentsClientHttpRequestFactory result = HttpUtils.buildPooledConnection(httpClientConfig, tlsSocketStrategy).build();

        // Then
        HttpClient httpClient = result.getHttpClient();
        Field connManagerField = httpClient.getClass().getDeclaredField("connManager");
        connManagerField.setAccessible(true);
        PoolingHttpClientConnectionManager pooledConnectionManager = (PoolingHttpClientConnectionManager) connManagerField.get(httpClient);
        assertHttpClientConnectionManager(pooledConnectionManager);
    }

    private static HttpClientConfig buildTestHttpClientConfig() {
        return HttpClientConfig.builder()
                .connectionPool(HttpClientConfig.HttpClientConnectionPoolConfig.builder()
                        .size(10)
                        .sizePerRoute(5)
                        .timeToLiveMinutes(3)
                        .build())
                .timeout(HttpClientConfig.HttpClientTimeoutConfig.builder()
                        .connectMillis(1000)
                        .readMillis(3000)
                        .build())
                .build();
    }

    @SuppressWarnings("unchecked")
    private static void assertHttpClientConnectionManager(PoolingHttpClientConnectionManager result) throws NoSuchFieldException, IllegalAccessException {
        Assertions.assertEquals(10, result.getMaxTotal());
        Assertions.assertEquals(5, result.getDefaultMaxPerRoute());

        Field connectionConfigResolverField = PoolingHttpClientConnectionManager.class.getDeclaredField("connectionConfigResolver");
        connectionConfigResolverField.setAccessible(true);
        Resolver<HttpRoute, ConnectionConfig> connectionConfigResolver = (Resolver<HttpRoute, ConnectionConfig>) connectionConfigResolverField.get(result);
        ConnectionConfig connectionConfig = connectionConfigResolver.resolve(null);

        Assertions.assertEquals(Timeout.ofMilliseconds(1_000), connectionConfig.getConnectTimeout());
        Assertions.assertEquals(Timeout.ofMilliseconds(3_000), connectionConfig.getSocketTimeout());
        Assertions.assertEquals(Timeout.ofMilliseconds(180_000), connectionConfig.getTimeToLive());
    }

    @Test
    void givenCorrectRequestWhenFetchFromPreSignedUrlThenOk() throws IOException, InterruptedException {
        //GIVEN
        try (MockedStatic<java.net.http.HttpClient> httpClientStaticMock = Mockito.mockStatic(java.net.http.HttpClient.class);
             MockedStatic<java.net.http.HttpRequest> httpRequestStaticMock = Mockito.mockStatic(java.net.http.HttpRequest.class);
             java.net.http.HttpClient httpClientMock = Mockito.mock(java.net.http.HttpClient.class)) {

            java.net.http.HttpRequest.Builder httpRequestBuilderMock = Mockito.mock(java.net.http.HttpRequest.Builder.class);
            java.net.http.HttpRequest httpRequestMock = Mockito.mock(java.net.http.HttpRequest.class);

            httpClientStaticMock.when(java.net.http.HttpClient::newHttpClient)
                    .thenReturn(httpClientMock);

            httpRequestStaticMock.when(() -> java.net.http.HttpRequest.newBuilder(Mockito.isA(URI.class)))
                    .thenReturn(httpRequestBuilderMock);
            Mockito.when(httpRequestBuilderMock.GET()).thenReturn(httpRequestBuilderMock);
            Mockito.when(httpRequestBuilderMock.build()).thenReturn(httpRequestMock);

            Path expectedPath = Mockito.mock(Path.class);

            HttpResponse<Path> httpResponseMock = HttpTestUtils.basicHttpOkResponse(expectedPath, null, null);
            Mockito.when(httpClientMock.send(Mockito.eq(httpRequestMock), Mockito.<HttpResponse.BodyHandler<Path>>any()))
                    .thenReturn(httpResponseMock);

            URI uri = Mockito.mock(URI.class);
            Path path = Mockito.mock(Path.class);

            //WHEN
            HttpResponse<Path> actualResult = HttpUtils.fetchFromPreSignedUrl(uri, path);

            //THEN
            Assertions.assertNotNull(actualResult);
            Assertions.assertSame(expectedPath, actualResult.body());
        }
    }

    @Test
    void givenExceptionWhenFetchFromPreSignedUrlThenThrowHttpPreSignedGetRequestException() {
        //GIVEN
        try (MockedStatic<java.net.http.HttpClient> httpClientStaticMock = Mockito.mockStatic(java.net.http.HttpClient.class)) {
            httpClientStaticMock.when(java.net.http.HttpClient::newHttpClient)
                    .thenThrow(new RuntimeException());

            URI uri = Mockito.mock(URI.class);
            Path path = Mockito.mock(Path.class);
            Path fileNamePath = Path.of("fileName.txt");
            Mockito.when(path.getFileName()).thenReturn(fileNamePath);

            //WHEN
            HttpUtils.HttpPreSignedGetRequestException httpPreSignedGetRequestException =
                    Assertions.assertThrows(HttpUtils.HttpPreSignedGetRequestException.class, () -> HttpUtils.fetchFromPreSignedUrl(uri, path));

            //THEN
            Assertions.assertEquals(
                "Error in downloading file %s".formatted(fileNamePath),
                httpPreSignedGetRequestException.getMessage()
            );
        }
    }
}
