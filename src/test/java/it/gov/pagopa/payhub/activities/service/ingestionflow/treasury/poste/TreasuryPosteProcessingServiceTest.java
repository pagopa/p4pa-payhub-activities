package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TreasuryPosteProcessingServiceTest {

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private TreasuryPosteErrorsArchiverService errorsArchiverServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private Path workingDirectory;
    @Mock
    private TreasuryPosteMapper mapperMock;
    @Mock
    private TreasuryService treasuryServiceMock;

    private TreasuryPosteProcessingService service;

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new TreasuryPosteProcessingService(1, mapperMock, treasuryServiceMock, errorsArchiverServiceMock, organizationServiceMock, fileExceptionHandlerService);
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                mapperMock,
                treasuryServiceMock,
                organizationServiceMock,
                errorsArchiverServiceMock
        );
    }

    @Test
    void whenGetSequencingIdThenReturnExpectedValue() {
        // Given
        TreasuryPosteIngestionFlowFileDTO row = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);

        // When
        String result = service.getSequencingId(row);

        // Then
        assertEquals(
                TreasuryUtils.getIdentificativo(row.getRemittanceDescription(), TreasuryUtils.IUF),
                result);
    }

    @Test
    void processTreasuryPosteWithNoErrors() {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        LocalDate billDate = LocalDate.of(2025, 9, 23);
        String billCode = TreasuryUtils.generateBillCode(iuf);
        String ipa = "IPA123";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(orgId, iuf)).thenReturn(null);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(iuf);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification)).thenReturn(mappedNotification);
        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(mapperMock).map(dto, iban, iuf, billCode, billDate, ingestionFlowFile);
        Mockito.verify(treasuryServiceMock).insert(mappedNotification);
    }

    @Test
    void givenThrowExceptionWhenProcessTreasuryPosteThenAddError() throws URISyntaxException {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        LocalDate billDate = LocalDate.of(2025, 9, 23);
        String billCode = TreasuryUtils.generateBillCode(iuf);
        String ipa = "IPA123";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        workingDirectory = Path.of(new URI("file:///tmp"));

        Treasury mappedNotification = mock(Treasury.class);
        Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification))
                .thenThrow(new RuntimeException("DUMMYPROCESSINGERROR"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(null);
        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(new CsvException("DUMMYERROR")),
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

        verify(mapperMock).map(dto, iban, iuf, billCode, billDate, ingestionFlowFile);
        verify(treasuryServiceMock).insert(mappedNotification);

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryPosteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(-1L)
                        .errorCode("CSV_GENERIC_ERROR")
                        .errorMessage("Errore generico nella lettura del file: DUMMYERROR")
                        .build(),
                TreasuryPosteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(2L)
                        .errorCode("PROCESSING_ERROR")
                        .errorMessage("DUMMYPROCESSINGERROR")
                        .iuf(iuf)
                        .build()
        ));
    }

    @Test
    void processTreasuryPosteWithExistingTreasury() {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        String ipa = "IPA123";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);
        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(),
                ingestionFlowFile, workingDirectory);

        //
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryPosteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF 2025-09-23BPPIITRRXXX-000038102790 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(iuf)
                        .build()
        ));
    }

    @Test
    void processTreasuryPosteWithExistingTreasurySameBillCodeAndYear() {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        LocalDate billDate = LocalDate.of(2025, 9, 23);
        String billCode = TreasuryUtils.generateBillCode(iuf);
        String ipa = "IPA123";
        Long orgId = 1L;

        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode(billCode);
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);
        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(iuf);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryServiceMock.insert(mappedNotification)).thenReturn(mappedNotification);

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock).map(dto, iban, iuf, billCode, billDate, ingestionFlowFile);
        Mockito.verify(treasuryServiceMock).insert(mappedNotification);
    }

    @Test
    void processTreasuryPosteWithExistingTreasuryDifferentBillCode() {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        String ipa = "IPA123";
        Long orgId = 1L;
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);

        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode("BILL123");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryPosteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF 2025-09-23BPPIITRRXXX-000038102790 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(iuf)
                        .build()
        ));
    }

    @Test
    void processTreasuryPosteWithExistingTreasuryDifferentYear() {
        // Given
        String iban = "IT84K0760101000000010123456";
        String iuf = "2025-09-23BPPIITRRXXX-000038102790";
        String dateString = "23/09/2025";
        String billCode = "8102790";
        String ipa = "IPA123";
        Long orgId = 1L;
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(orgId);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(orgId);

        TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
        dto.setBillDate(dateString);
        dto.setRegionValueDate(dateString);
        dto.setCreditBillAmount(null);
        dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode(billCode);
        existingTreasuryIuf.setBillYear("2024");
        Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

        Mockito.when(organizationServiceMock.getOrganizationById(any()))
                .thenReturn(Optional.of(organization));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
                Stream.of(dto).iterator(), iban, List.of(),
                ingestionFlowFile, workingDirectory);

        //Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                TreasuryPosteErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("IUF_ALREADY_ASSOCIATED")
                        .errorMessage("Lo IUF 2025-09-23BPPIITRRXXX-000038102790 e' gia' associato ad un'altra tesoreria per l'ente con codice IPA IPA123")
                        .iuf(iuf)
                        .build()
        ));
    }
}