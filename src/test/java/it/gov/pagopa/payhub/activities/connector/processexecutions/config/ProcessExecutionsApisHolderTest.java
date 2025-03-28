package it.gov.pagopa.payhub.activities.connector.processexecutions.config;

import static org.mockito.Mockito.when;

import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import java.time.LocalDateTime;
import java.util.List;
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
class ProcessExecutionsApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private ProcessExecutionsApisHolder processExecutionsApisHolder;

    @BeforeEach
    void setUp() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());
        ProcessExecutionsApiClientConfig clientConfig = ProcessExecutionsApiClientConfig.builder()
                .baseUrl("http://example.com")
                .build();
        processExecutionsApisHolder = new ProcessExecutionsApisHolder(clientConfig, restTemplateBuilderMock);
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
                accessToken -> processExecutionsApisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                            .crudGetIngestionflowfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                processExecutionsApisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> processExecutionsApisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                            .updateStatus(1L, IngestionFlowFileStatus.UPLOADED, IngestionFlowFileStatus.PROCESSING, "message", "error"),
                new ParameterizedTypeReference<>() {},
                processExecutionsApisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> processExecutionsApisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                    .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(String.valueOf(1L), List.of(IngestionFlowFileTypeEnum.PAYMENTS_REPORTING.getValue()), LocalDateTime.now().minusDays(1L), null, null, null, null, null, null, null),
            new ParameterizedTypeReference<>() {},
            processExecutionsApisHolder::unload);
    }

    @Test
    void whenGetPaidExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> processExecutionsApisHolder.getPaidExportFileEntityControllerApi(accessToken)
                        .crudGetPaidexportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                processExecutionsApisHolder::unload);
    }

    @Test
    void whenGetExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> processExecutionsApisHolder.getExportFileEntityControllerApi(accessToken)
                        .crudGetExportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                processExecutionsApisHolder::unload);
    }

    @Test
    void whenGetExportFileEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> processExecutionsApisHolder.getExportFileEntityExtendedControllerApi(accessToken)
                        .updateExportFileStatus(1L, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, ""),
                new ParameterizedTypeReference<>() {},
                processExecutionsApisHolder::unload);
    }
}