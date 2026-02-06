package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csvcomplete;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete.TreasuryCsvCompleteMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
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
    private TreasuryService treasuryServiceMock;

    private TreasuryCsvCompleteProcessingService service;

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new TreasuryCsvCompleteProcessingService(1,
                mapperMock,
                errorsArchiverServiceMock,
                treasuryServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                errorsArchiverServiceMock,
                organizationServiceMock,
                mapperMock,
                treasuryServiceMock
        );
    }

    @Test
    void whenGetSequencingIdThenReturnExpectedValue() {
        // Given
        TreasuryCsvCompleteIngestionFlowFileDTO row = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);

        // When
        String result = service.getSequencingId(row);

        // Then
        assertEquals(
                row.getBillCode() + "-" + row.getBillYear(),
                result);
    }

    @Test
    void processTreasuryCsvCompleteWithOrganizationErrors() {
        // Given
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

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA IPA123 dell'ente non corrisponde a quello del file IPA123_WRONG")
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        ));
    }

    @Test
    void processTreasuryCsvCompleteWithNoErrors() {
        // Given
        String ipa = "IPA123";
        String iuf = "IUF12345";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf(iuf);
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, iuf)).thenReturn(null);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(iuf);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification)).thenReturn(mappedNotification);

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void processTreasuryCsvCompleteWithNoErrorsAndNullIuf() {
        // Given
        String ipa = "IPA123";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf(null);
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);

        Mockito.when(organizationServiceMock.getOrganizationById(orgId))
                .thenReturn(Optional.of(organization));

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(null);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification)).thenReturn(mappedNotification);

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void givenThrowExceptionWhenProcessTreasuryCsvCompleteThenAddError() throws URISyntaxException {
        // Given
        String ipa = "IPA123";
        Long orgId = 1L;
        String iuf = "IUF12345";

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setOrganizationIpaCode(ipa);
        dto.setIuf(iuf);
        dto.setIuv("IUV12345");

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        workingDirectory = Path.of(new URI("file:///tmp"));

        Treasury mappedNotification = mock(Treasury.class);
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification))
                .thenThrow(new RuntimeException("DUMMYPROCESSINGERROR"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(null);

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
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

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(-1L)
                        .errorCode("CSV_GENERIC_ERROR")
                        .errorMessage("Errore generico nella lettura del file: DUMMYERROR")
                        .build(),
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(2L)
                        .errorCode("PROCESSING_ERROR")
                        .errorMessage("DUMMYPROCESSINGERROR")
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        ));
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasury() {
        // Given
        String ipa = "IPA123";
        long orgId = 1L;
        String iuf = "IUF12345";

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setOrganizationIpaCode(ipa);
        dto.setIuf(iuf);
        dto.setIuv("IUV12345");

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, iuf)).thenReturn(existingTreasuryIuf);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF IUF12345 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        ));
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasurySameBillCodeAndYear() {
        // Given
        String ipa = "IPA123";
        long orgId = 1L;
        String iuf = "IUF12345";

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf(iuf);
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);
        dto.setBillCode("BILL123");

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode("BILL123");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, iuf)).thenReturn(existingTreasuryIuf);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(iuf);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification)).thenReturn(mappedNotification);

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasuryDifferentBillCode() {
        // Given
        String ipa = "IPA123";
        long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);
        dto.setBillCode("BILL123");

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(dto.getIuf());
        existingTreasuryIuf.setBillCode("BILL999");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, dto.getIuf())).thenReturn(existingTreasuryIuf);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(orgId, result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF IUF12345 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        ));
    }

    @Test
    void processTreasuryCsvCompleteWithExistingTreasuryDifferentYear() {
        // Given
        String ipa = "IPA123";
        long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(Optional.of(organization));

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvCompleteIngestionFlowFileDTO.class);
        dto.setBillYear("2026");
        dto.setIuf("IUF12345");
        dto.setIuv("IUV12345");
        dto.setOrganizationIpaCode(ipa);
        dto.setBillCode("BILL123");

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(dto.getIuf());
        existingTreasuryIuf.setBillCode(dto.getBillCode());
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, dto.getIuf())).thenReturn(existingTreasuryIuf);

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsvComplete(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryCsvCompleteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF IUF12345 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(dto.getIuf())
                        .iuv(dto.getIuv())
                        .build()
        ));
    }
}
