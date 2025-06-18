package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TreasuryCsvCompleteProcessingServiceTest {

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private TreasuryCsvCompleteErrorsArchiverService errorsArchiverServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @Mock
    private Path workingDirectory;

    @Mock
    private TreasuryCsvCompleteMapper mapperMock;

    @Mock
    private TreasuryService treasuryService;

    private TreasuryCsvCompleteProcessingService service;

    @BeforeEach
    void setUp() {
        service = new TreasuryCsvCompleteProcessingService(mapperMock, errorsArchiverServiceMock, treasuryService, organizationServiceMock);
    }

    @Test
    void processTreasuryCsvCompleteWithOrganizationErrors() {

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");

        String ipa = "IPA123";
        dto.setOrganizationIpaCode(ipa);

        String ipaWrong = "IPA123_WRONG";

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipaWrong);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
    }

    @Test
    void processTreasuryCsvCompleteWithNoErrors() {

        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipa);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);

        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void givenThrowExceptionWhenProcessTreasuryCsvCompleteThenAddError() throws URISyntaxException {

        String ipa = "123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);

        TreasuryCsvCompleteIngestionFlowFileDTO paymentNotificationIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        paymentNotificationIngestionFlowFileDTO.setBillYear("2025");
        paymentNotificationIngestionFlowFileDTO.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getIpaCodeByOrganizationId(any())).thenReturn(ipa);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(Long.parseLong(ipa));
        workingDirectory = Path.of(new URI("file:///tmp"));

        Treasury mappedNotification = mock(Treasury.class);
        Mockito.when(mapperMock.map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(paymentNotificationIngestionFlowFileDTO).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        verify(mapperMock).map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile);
        verify(treasuryService).insert(mappedNotification);
    }
}
