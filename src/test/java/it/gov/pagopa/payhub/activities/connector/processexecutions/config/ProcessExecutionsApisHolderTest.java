package it.gov.pagopa.payhub.activities.connector.processexecutions.config;

import it.gov.pagopa.payhub.activities.config.json.JsonConfig;
import it.gov.pagopa.payhub.activities.connector.BaseApiHolderTest;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile.IngestionFlowFileTypeEnum;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFileUpdateStatusRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessExecutionsApisHolderTest extends BaseApiHolderTest {

    @Mock
    private RestTemplateBuilder restTemplateBuilderMock;

    private ProcessExecutionsApisHolder apisHolder;
    private ProcessExecutionsApiClientConfig apiClientConfig;

    @BeforeEach
    void init() {
        when(restTemplateBuilderMock.build()).thenReturn(restTemplateMock);
        when(restTemplateMock.getUriTemplateHandler()).thenReturn(new DefaultUriBuilderFactory());

        apiClientConfig = ProcessExecutionsApiClientConfig.builder()
                .baseUrl("http://example.com")
                .maxAttempts(3)
                .build();
        apisHolder = new ProcessExecutionsApisHolder(apiClientConfig, restTemplateBuilderMock, new JsonConfig().objectMapperJackson3());

        verifyHttpClientErrorJsonBodyHandlerConfiguration(apisHolder.getExportFileEntityControllerApi(null));
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                restTemplateBuilderMock,
                restTemplateMock
        );
    }

    @Test
    void testRetryConfiguration() {
        assertRetry(apiClientConfig,
                accessToken -> apisHolder.getClassificationsExportFileEntityControllerApi(accessToken)
                        .crudGetClassificationsexportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {}
        );
    }

    @Test
    void whenGetIngestionFlowFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getIngestionFlowFileEntityControllerApi(accessToken)
                            .crudGetIngestionflowfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        IngestionFlowFileUpdateStatusRequestDTO ingestionFlowFileUpdateStatusRequestDTO = new IngestionFlowFileUpdateStatusRequestDTO();
        ingestionFlowFileUpdateStatusRequestDTO.setOldStatus(IngestionFlowFileStatus.UPLOADED);
        ingestionFlowFileUpdateStatusRequestDTO.setNewStatus(IngestionFlowFileStatus.PROCESSING);
        ingestionFlowFileUpdateStatusRequestDTO.setProcessedRows(0L);
        ingestionFlowFileUpdateStatusRequestDTO.setTotalRows(0L);
        ingestionFlowFileUpdateStatusRequestDTO.setFileVersion("1.0");
        ingestionFlowFileUpdateStatusRequestDTO.setErrorDescription("message");
        ingestionFlowFileUpdateStatusRequestDTO.setDiscardFile("error");

        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getIngestionFlowFileEntityExtendedControllerApi(accessToken)
                            .updateStatus(1L, ingestionFlowFileUpdateStatusRequestDTO),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetIngestionFlowFileSearchControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
            accessToken -> apisHolder.getIngestionFlowFileSearchControllerApi(accessToken)
                    .crudIngestionFlowFilesFindByOrganizationIDFlowTypeCreateDate(1L, List.of(IngestionFlowFileTypeEnum.PAYMENTS_REPORTING.getValue()), LocalDateTime.now().minusDays(1L), LocalDateTime.now().minusDays(1L), null, null, null, null, null, null),
            new ParameterizedTypeReference<>() {},
            apisHolder::unload);
    }

    @Test
    void whenGetPaidExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getPaidExportFileEntityControllerApi(accessToken)
                        .crudGetPaidexportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getExportFileEntityControllerApi(accessToken)
                        .crudGetExportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetExportFileEntityExtendedControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getExportFileEntityExtendedControllerApi(accessToken)
                        .updateExportFileStatus(1L, ExportFileStatus.COMPLETED, ExportFileStatus.EXPIRED, "filePath", "fileName", 20L,2L, "", OffsetDateTime.now().plusDays(5L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetReceiptsArchivingExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getReceiptsArchivingExportFileEntityControllerApi(accessToken)
                        .crudGetReceiptsarchivingexportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }

    @Test
    void whenGetClassificationsExportFileEntityControllerApiThenAuthenticationShouldBeSetInThreadSafeMode() throws InterruptedException {
        assertAuthenticationShouldBeSetInThreadSafeMode(
                accessToken -> apisHolder.getClassificationsExportFileEntityControllerApi(accessToken)
                        .crudGetClassificationsexportfile(String.valueOf(1L)),
                new ParameterizedTypeReference<>() {},
                apisHolder::unload);
    }
}