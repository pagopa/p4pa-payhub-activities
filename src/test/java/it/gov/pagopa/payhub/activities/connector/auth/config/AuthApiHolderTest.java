package it.gov.pagopa.payhub.activities.connector.auth.config;

import it.gov.pagopa.pu.auth.dto.generated.AccessToken;
import it.gov.pagopa.pu.auth.dto.generated.UserInfo;
import it.gov.pagopa.pu.auth.generated.ApiClient;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
class AuthApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;
    @Mock
    private RestTemplate restTemplateMock;

    private AuthApisHolder authApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ApiClient apiClient = new ApiClient(restTemplateMock);
        String baseUrl = "http://example.com";
        apiClient.setBasePath(baseUrl);
        authApisHolder = new AuthApisHolder(baseUrl, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetAuthzApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        authenticationShouldBeSetInThreadSafeMode(
                accessToken -> authApisHolder.getAuthzApi(accessToken)
                        .getUserInfoFromMappedExternaUserId("externalUserId"),
                UserInfo.class);
    }

    @Test
    void whenGetAuthnApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        authenticationShouldBeSetInThreadSafeMode(
                accessToken -> authApisHolder.getAuthnApi(accessToken)
                        .postToken("clientId", "grantType", "scope", "subjectToken", "subjectIssuer", "subjectTokenType", "clientSecret"),
                AccessToken.class);
    }

    <T> void authenticationShouldBeSetInThreadSafeMode(Function<String, T> apiInvoke, Class<T> apiReturnedType) throws InterruptedException {
        // Configuring useCases in a single thread
        List<Pair<String, T>> useCases = IntStream.rangeClosed(0, 100)
                .mapToObj(i -> {
                    try {
                        String accessToken = "accessToken" + i;
                        T expectedResult = apiReturnedType.getConstructor().newInstance();

                        Mockito.doReturn(ResponseEntity.ok(expectedResult))
                                .when(restTemplateMock)
                                .exchange(
                                        Mockito.argThat(req ->
                                                req.getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, Collections.emptyList()).getFirst()
                                                        .equals("Bearer " + accessToken)),
                                        Mockito.eq(apiReturnedType));
                        return Pair.of(accessToken, expectedResult);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .toList();

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            executorService.invokeAll(useCases.stream()
                    .map(p -> (Callable<?>) () -> {
                        // Given
                        String accessToken = p.getKey();
                        T expectedResult = p.getValue();

                        // When
                        T result = apiInvoke.apply(accessToken);

                        // Then
                        Assertions.assertSame(expectedResult, result);
                        return true;
                    })
                    .toList());
        }

        authApisHolder.unload();

        Mockito.verify(restTemplateMock, Mockito.times(useCases.size()))
                .exchange(Mockito.any(), Mockito.<ParameterizedTypeReference<?>>any());
    }
}
