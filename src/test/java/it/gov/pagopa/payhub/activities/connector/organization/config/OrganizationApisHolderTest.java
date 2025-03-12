package it.gov.pagopa.payhub.activities.connector.organization.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

@ExtendWith(MockitoExtension.class)
class OrganizationApisHolderTest extends BaseApiHolderTest {
    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private OrganizationApisHolder organizationApisHolder;

    @BeforeEach
    void setUp() {
        Mockito.when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        Mockito.when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        OrganizationApiClientConfig clientConfig = OrganizationApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        organizationApisHolder = new OrganizationApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetOrganizationSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> organizationApisHolder.getOrganizationSearchControllerApi(accessToken)
                        .crudOrganizationsFindByIpaCode("ORGIPACODE"),
                new ParameterizedTypeReference<>() {},
                organizationApisHolder::unload);
    }

	@Test
	void whenGetBrokerEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> organizationApisHolder.getBrokerEntityControllerApi(accessToken)
                .crudGetBrokers(0, 2_000, null),
            new ParameterizedTypeReference<>() {},
            organizationApisHolder::unload);
	}
}
