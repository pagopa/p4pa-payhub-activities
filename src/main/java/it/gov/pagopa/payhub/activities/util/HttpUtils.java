package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.config.rest.HttpClientConfig;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpComponentsClientHttpRequestFactoryBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;

public class HttpUtils {
    private HttpUtils() {
    }

    public static PoolingHttpClientConnectionManagerBuilder getPooledConnectionManagerBuilder(HttpClientConfig httpClientConfig, TlsSocketStrategy tlsSocketStrategy) {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setTlsSocketStrategy(tlsSocketStrategy)
                .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                .setConnPoolPolicy(PoolReusePolicy.LIFO)
                .setMaxConnPerRoute(httpClientConfig.getConnectionPool().getSizePerRoute())
                .setMaxConnTotal(httpClientConfig.getConnectionPool().getSize())
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setSocketTimeout(Timeout.ofMilliseconds(httpClientConfig.getTimeout().getReadMillis()))
                        .setConnectTimeout(Timeout.ofMilliseconds(httpClientConfig.getTimeout().getConnectMillis()))
                        .setTimeToLive(TimeValue.ofMinutes(httpClientConfig.getConnectionPool().getTimeToLiveMinutes()))
                        .build());
    }

    public static HttpComponentsClientHttpRequestFactoryBuilder buildPooledConnection(HttpClientConfig httpClientConfig, TlsSocketStrategy tlsSocketStrategy) {
        return ClientHttpRequestFactoryBuilder.httpComponents()
                .withHttpClientCustomizer(configurer -> configurer
                        .setConnectionManager(getPooledConnectionManagerBuilder(httpClientConfig, tlsSocketStrategy).build()));
    }

    public static HttpResponse<Path> fetchFromPreSignedUrl(URI preSignedURI, Path pathFile) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest getRequest = HttpRequest.newBuilder(preSignedURI).GET().build();
            return httpClient.send(getRequest, HttpResponse.BodyHandlers.ofFile(pathFile));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error in downloading file %s from URI %s".formatted(
                pathFile.getFileName(),
                stripQueryParamFromPreSignedURI(preSignedURI).orElse("unknownUrl")
            ));
        }
    }

    private static Optional<String> stripQueryParamFromPreSignedURI(URI preSignedUrl) {
        try {
            return Optional.of(new URI(
                preSignedUrl.getScheme(),
                preSignedUrl.getAuthority(),
                preSignedUrl.getPath(),
                null,
                null
            ).toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
