package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.poste;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.poste.TreasuryPosteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.poste.TreasuryPosteMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

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
  private TreasuryService treasuryService;

  @Mock
  private FileExceptionHandlerService fileExceptionHandlerServiceMock;

  private TreasuryPosteProcessingService service;

  @BeforeEach
  void setUp() {
    service = new TreasuryPosteProcessingService(mapperMock, treasuryService, errorsArchiverServiceMock, organizationServiceMock, fileExceptionHandlerServiceMock);
  }

  @Test
  void processTreasuryPosteWithNoErrors() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "2025-09-23BPPIITRRXXX-000038102790";
    String dateString = "23/09/2025";
    LocalDate billDate = LocalDate.of(2025, 9, 23);
    String billCode = TreasuryUtils.generateBillCode(iuf);

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto.setBillDate(dateString);
    dto.setRegionValueDate(dateString);
    dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

    Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
    mappedNotification.setIuf(iuf);
    mappedNotification.setTreasuryId("TREASURY_ID_1");
    Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
    Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

    TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
        Stream.of(dto).iterator(), iban, List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Mockito.verify(mapperMock).map(dto, iban, iuf, billCode, billDate, ingestionFlowFile);
    Mockito.verify(treasuryService).insert(mappedNotification);
  }

  @Test
  void givenThrowExceptionWhenProcessTreasuryPosteThenAddError() throws URISyntaxException {
    String iban = "IT84K0760101000000010123456";
    String iuf = "2025-09-23BPPIITRRXXX-000038102790";
    String dateString = "23/09/2025";
    LocalDate billDate = LocalDate.of(2025, 9, 23);
    String billCode = TreasuryUtils.generateBillCode(iuf);

    TreasuryPosteIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto.setBillDate(dateString);
    dto.setRegionValueDate(dateString);
    dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    workingDirectory = Path.of(new URI("file:///tmp"));

    Treasury mappedNotification = mock(Treasury.class);
    Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
    Mockito.when(treasuryService.insert(mappedNotification))
        .thenThrow(new RuntimeException("Processing error"));

    Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
        .thenReturn("zipFileName.csv");

    Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(null);

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
    verify(treasuryService).insert(mappedNotification);
  }

  @Test
  void processTreasuryPosteWithExistingTreasury() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "2025-09-23BPPIITRRXXX-000038102790";
    String dateString = "23/09/2025";

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto.setBillDate(dateString);
    dto.setRegionValueDate(dateString);
    dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

    TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
    existingTreasuryIuf.setIuf(iuf);
    Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

    TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
        Stream.of(dto).iterator(), iban, List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(0L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
    Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
    Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());
  }

  @Test
  void processTreasuryPosteWithExistingTreasurySameBillCodeAndYear() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "2025-09-23BPPIITRRXXX-000038102790";
    String dateString = "23/09/2025";
    LocalDate billDate = LocalDate.of(2025, 9, 23);
    String billCode = TreasuryUtils.generateBillCode(iuf);

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto.setBillDate(dateString);
    dto.setRegionValueDate(dateString);
    dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");
    
    TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
    existingTreasuryIuf.setIuf(iuf);
    existingTreasuryIuf.setBillCode(billCode);
    existingTreasuryIuf.setBillYear("2025");
    Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

    Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
    mappedNotification.setIuf(iuf);
    mappedNotification.setTreasuryId("TREASURY_ID_1");
    Mockito.when(mapperMock.map(dto, iban, iuf, billCode, billDate, ingestionFlowFile)).thenReturn(mappedNotification);
    Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

    TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
        Stream.of(dto).iterator(), iban, List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(1L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
    Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
    Mockito.verify(mapperMock).map(dto, iban, iuf, billCode, billDate, ingestionFlowFile);
    Mockito.verify(treasuryService).insert(mappedNotification);
  }

  @Test
  void processTreasuryPosteWithExistingTreasuryDifferentBillCodeOrYear() {
    String iban = "IT84K0760101000000010123456";
    String iuf = "2025-09-23BPPIITRRXXX-000038102790";
    String dateString = "23/09/2025";
    String billCode = "8102790";

    IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
    TreasuryPosteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto.setBillDate(dateString);
    dto.setRegionValueDate(dateString);

    dto.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

    TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
    existingTreasuryIuf.setIuf(iuf);
    existingTreasuryIuf.setBillCode("BILL123");
    existingTreasuryIuf.setBillYear("2025");
    Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

    TreasuryIufIngestionFlowFileResult result = service.processTreasuryPoste(
        Stream.of(dto).iterator(), iban, List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
    Assertions.assertEquals(0L, result.getProcessedRows());
    Assertions.assertEquals(1L, result.getTotalRows());
    Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
    Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
    Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());

    TreasuryPosteIngestionFlowFileDTO dto2 = podamFactory.manufacturePojo(TreasuryPosteIngestionFlowFileDTO.class);
    dto2.setBillDate(dateString);
    dto2.setRegionValueDate(dateString);
    dto2.setCreditBillAmount(null);
    dto2.setRemittanceDescription("RI1/PUR/LGPE-RIVERSAMENTO/URI/2025-09-23BPPIITRRXXX-000038102790 ACCREDITO BOLLETTINO P.A.");

    TreasuryIuf existingTreasuryIuf2 = new TreasuryIuf();
    existingTreasuryIuf2.setIuf(iuf);
    existingTreasuryIuf2.setBillCode(billCode);
    existingTreasuryIuf2.setBillYear("2024");
    Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf2);

    TreasuryIufIngestionFlowFileResult result2 = service.processTreasuryPoste(
        Stream.of(dto2).iterator(), iban, List.of(),
        ingestionFlowFile, workingDirectory);

    Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result2.getOrganizationId());
    Assertions.assertEquals(0L, result2.getProcessedRows());
    Assertions.assertEquals(1L, result2.getTotalRows());
    Assertions.assertNotNull(result2.getIuf2TreasuryIdMap());
    Assertions.assertEquals(0, result2.getIuf2TreasuryIdMap().size());
    Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());
  }
}