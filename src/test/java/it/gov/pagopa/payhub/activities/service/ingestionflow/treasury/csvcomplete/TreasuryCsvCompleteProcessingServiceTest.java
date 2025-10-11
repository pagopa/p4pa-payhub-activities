package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
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
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

        Organization organization = new Organization();
        organization.setIpaCode(ipa + "_WRONG");
        Long orgId = ingestionFlowFile.getOrganizationId();
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);
        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);


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
        Long orgId = 1L;
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf("IUF12345");
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
        Mockito.verify(treasuryService).insert(mappedNotification);
    }

    @Test
    void processTreasuryCsvCompleteWithNoErrorsAndNullIuf() {

        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        Long orgId = 1L;
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf(null);
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf("IUF12345");
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
        Mockito.verify(treasuryService).insert(mappedNotification);
    }

    @Test
    void givenThrowExceptionWhenProcessTreasuryCsvCompleteThenAddError() throws URISyntaxException {

        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        Long orgId = 1L;
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);

        TreasuryCsvCompleteIngestionFlowFileDTO paymentNotificationIngestionFlowFileDTO = TestUtils.getPodamFactory().manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        paymentNotificationIngestionFlowFileDTO.setBillYear("2025");
        paymentNotificationIngestionFlowFileDTO.setOrganizationIpaCode(ipa);
        paymentNotificationIngestionFlowFileDTO.setIuf("IUF12345");
        paymentNotificationIngestionFlowFileDTO.setIuv("IUV12345");

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        Treasury mappedNotification = mock(Treasury.class);
        Mockito.when(mapperMock.map(paymentNotificationIngestionFlowFileDTO, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, "IUF12345")).thenReturn(null);

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

    @Test
    void processTreasuryCsvCompleteWithExistingTreasury() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setOrganizationIpaCode(ipa);
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf("IUF12345");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, "IUF12345")).thenReturn(existingTreasuryIuf);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasurySameBillCodeAndYear() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);
        dto.setBillCode("BILL123");

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf("IUF12345");
        existingTreasuryIuf.setBillCode("BILL123");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, "IUF12345")).thenReturn(existingTreasuryIuf);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf("IUF12345");
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
        Mockito.verify(treasuryService).insert(mappedNotification);
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasuryDifferentBillCodeOrYear() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);
        dto.setBillCode("BILL123");

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf("IUF12345");
        existingTreasuryIuf.setBillCode("BILL999");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, "IUF12345")).thenReturn(existingTreasuryIuf);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
        Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());

        TreasuryCsvCompleteIngestionFlowFileDTO dto2 = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto2.setBillYear("2026");
        dto2.setIuf("IUF12345");
        dto2.setIuv("IUV12345");
        dto2.setOrganizationIpaCode(ipa);
        dto2.setBillCode("BILL123");

        TreasuryIuf existingTreasuryIuf2 = new TreasuryIuf();
        existingTreasuryIuf2.setIuf("IUF12345");
        existingTreasuryIuf2.setBillCode("BILL123");
        existingTreasuryIuf2.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, "IUF12345")).thenReturn(existingTreasuryIuf2);

        TreasuryIufIngestionFlowFileResult result2 = service.processTreasuryCsvComplete(
                Stream.of(dto2).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result2.getOrganizationId());
        Assertions.assertEquals(0L, result2.getProcessedRows());
        Assertions.assertEquals(1L, result2.getTotalRows());
        Assertions.assertNotNull(result2.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result2.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
        Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());
    }
}
