package it.gov.pagopa.payhub.activities.connector.processexecutions.config;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.FlowFileTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessExecutionsApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private ProcessExecutionsApisHolder ingestionFlowFileApisHolder;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ProcessExecutionsClientConfig clientConfig = ProcessExecutionsClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        ingestionFlowFileApisHolder = new ProcessExecutionsApisHolder(clientConfig, restTemplateBuilderMock);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void whenGetIngestionFlowFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    ingestionFlowFileApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                            .crudGetIngestionflowfile(String.valueOf(1L));
                    return null;
                },
                String.class,
                ingestionFlowFileApisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> {
                    ingestionFlowFileApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                            .updateStatus(1L, "oldStatus", "newStatus", "message", "error");
                    return null;
                },
                String.class,
                ingestionFlowFileApisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> {
                ingestionFlowFileApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                    .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(1L), List.of(FlowFileTypeEnum.PAYMENTS_REPORTING.getValue()), LocalDateTime.now().minusDays(1L), null, null, null, null, null, null, null);
                return null;
            },
            String.class,
            ingestionFlowFileApisHolder::unload);
    }
}