package it.gov.pagopa.payhub.activities.connector.auth.config;

import it.gov.pagopa.pu.p4paauth.generated.ApiClient;
import it.gov.pagopa.pu.p4paauth.generated.BaseApi;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Lazy
@Service
public abstract class AuthApiHolder<T extends BaseApi> {

    private final T authApi;

    private final ThreadLocal<String> bearerTokenHolder = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    protected AuthApiHolder(
            @Value("${rest.auth.base-url}") String baseUrl,

            RestTemplateBuilder restTemplateBuilder) {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.setBearerToken(bearerTokenHolder::get);

        Class<T> authClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), AuthApiHolder.class);
        try {
            authApi = Objects.requireNonNull(authClass).getConstructor(ApiClient.class).newInstance(apiClient);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot construct BaseApi instance using Class " + authClass, e);
        }
    }

    @PreDestroy
    public void unload(){
        bearerTokenHolder.remove();
    }

    /** It will return a {@link T} instrumented with the provided accessToken. Use null if auth is not required */
    public T getAuthApi(String accessToken){
        bearerTokenHolder.set(accessToken);
        return authApi;
    }
}
