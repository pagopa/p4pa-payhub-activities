package it.gov.pagopa.payhub.activities.connector;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.ApiClientExt;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class BaseApiHolderTest {

    @Mock
    protected RestTemplate restTemplateMock;
    @Mock
    protected Void voidMock;

    protected <T> void assertAuthenticationShouldBeSetInThreadSafeMode(Function<String, T> apiInvoke, ParameterizedTypeReference<T> apiReturnedType, Runnable apiUnloader) throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode((accessToken, userId) -> apiInvoke.apply(accessToken), apiReturnedType, apiUnloader, false);
    }

    @SuppressWarnings("unchecked")
    protected <T> void assertAuthenticationShouldBeSetInThreadSafeMode(BiFunction<String, String, T> apiInvoke, ParameterizedTypeReference<T> apiReturnedType, Runnable apiUnloader, boolean expecteUserIdHeader) throws InterruptedException {
        // Configuring useCases in a single thread
        List<Triple<String, String, T>> useCases = IntStream.rangeClosed(0, 100)
                .mapToObj(i -> {
                    try {
                        String accessToken = "accessToken" + i;
                        String userId = "userId" + i;
                        T expectedResult =
                                String.class.equals(apiReturnedType.getType()) ? (T)"RESULT"
                                : Integer.class.equals(apiReturnedType.getType()) ? (T)Integer.valueOf(0)
                                : Long.class.equals(apiReturnedType.getType()) ? (T)Long.valueOf(0L)
                                : apiReturnedType.getType().getTypeName().startsWith(List.class.getName()) ? (T) List.of()
                                : Void.class.equals(apiReturnedType.getType()) ? (T) voidMock
                                : (T) Mockito.mock(Class.forName(apiReturnedType.getType().getTypeName()));

                        Mockito.doReturn(ResponseEntity.ok(expectedResult))
                                .when(restTemplateMock)
                                .exchange(
                                        Mockito.argThat(req ->
                                                req.getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, Collections.emptyList()).getFirst()
                                                        .equals("Bearer " + accessToken) &&
                                                        (
                                                                !expecteUserIdHeader
                                                                        ||
                                                                        req.getHeaders().getOrDefault(ApiClientExt.HEADER_USER_ID, Collections.emptyList()).getFirst()
                                                                                .equals(userId)
                                                        )
                                        ),
                                        Mockito.eq(apiReturnedType));
                        return Triple.of(accessToken, userId, expectedResult);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                .toList();

        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            executorService.invokeAll(useCases.stream()
                            .map(p -> (Callable<?>) () -> {
                                // Given
                                String accessToken = p.getLeft();
                                String userId = p.getMiddle();
                                T expectedResult = p.getRight();

                                // When
                                T result = apiInvoke.apply(accessToken, userId);

                                // Then
                                Assertions.assertSame(expectedResult, result);
                                return true;
                            })
                            .toList())
                    .forEach(future -> {
                        try {
                            future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        apiUnloader.run();

        Mockito.verify(restTemplateMock, Mockito.times(useCases.size()))
                .exchange(Mockito.any(), Mockito.<ParameterizedTypeReference<?>>any());
    }
}
