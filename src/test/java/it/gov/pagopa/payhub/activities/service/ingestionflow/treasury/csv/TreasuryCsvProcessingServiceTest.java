package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.csv;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv.TreasuryCsvMapper;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
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

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TreasuryCsvProcessingServiceTest {
    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Mock
    private TreasuryCsvErrorsArchiverService errorsArchiverServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @Mock
    private Path workingDirectory;

    @Mock
    private TreasuryCsvMapper mapperMock;

    @Mock
    private TreasuryService treasuryService;

    private TreasuryCsvProcessingService service;

    @BeforeEach
    void setUp() {
        service = new TreasuryCsvProcessingService(mapperMock, errorsArchiverServiceMock, treasuryService, organizationServiceMock);
    }

    @Test
    void givenValidInputWhenProcessTreasuryCsvThenCompleteWithNoErrors() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        Long orgId = 1L;
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setBillCode("112233");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf("IUF12345");
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsv(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(1L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
        Mockito.verify(treasuryService).insert(mappedNotification);
    }

    @Test
    void givenThrowExceptionWhenProcessTreasuryCsvThenAddError() throws URISyntaxException {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        Long orgId = 1L;
        organization.setOrganizationId(orgId);
        Optional<Organization> organizationOptional = Optional.of(organization);

        TreasuryCsvIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setBillCode("112233");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        workingDirectory = Path.of(new URI("file:///tmp"));

        Treasury mappedNotification = mock(Treasury.class);
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification))
                .thenThrow(new RuntimeException("Processing error"));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF)))
                .thenReturn(null);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsv(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile,
                workingDirectory
        );

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

        verify(mapperMock).map(dto, ingestionFlowFile);
        verify(treasuryService).insert(mappedNotification);
    }

    @Test
    void givenExistingTreasuryWhenProcessTreasuryCsvThenCompleteWithoutProcessing() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setBillCode("112233");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        String iuf = TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsv(
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
    void givenExistingTreasuryWithSameBillCodeAndYearWhenProcessTreasuryCsvThenCompleteWithProcessing() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setBillCode("BILL123");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        String iuf = TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode("BILL123");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

        Treasury mappedNotification = podamFactory.manufacturePojo(Treasury.class);
        mappedNotification.setIuf(iuf);
        mappedNotification.setTreasuryId("TREASURY_ID_1");
        Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(mappedNotification);
        Mockito.when(treasuryService.insert(mappedNotification)).thenReturn(mappedNotification);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsv(
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
    void givenExistingTreasuryWithDifferentBillCodeOrYearWhenProcessTreasuryCsvThenCompleteWithoutProcessing() {
        String ipa = "IPA123";
        Organization organization = new Organization();
        organization.setIpaCode(ipa);
        organization.setOrganizationId(1L);
        Optional<Organization> organizationOptional = Optional.of(organization);

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto.setBillYear("2025");
        dto.setBillCode("BILL123");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

        String iuf = TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF);

        TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
        existingTreasuryIuf.setIuf(iuf);
        existingTreasuryIuf.setBillCode("BILL999");
        existingTreasuryIuf.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

        TreasuryIufIngestionFlowFileResult result = service.processTreasuryCsv(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
        Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
        Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
        Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
        Mockito.verify(treasuryService, Mockito.never()).insert(Mockito.any());

        TreasuryCsvIngestionFlowFileDTO dto2 = podamFactory.manufacturePojo(TreasuryCsvIngestionFlowFileDTO.class);
        dto2.setBillYear("2026");
        dto.setBillCode("BILL123");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        TreasuryIuf existingTreasuryIuf2 = new TreasuryIuf();
        existingTreasuryIuf2.setIuf(iuf);
        existingTreasuryIuf2.setBillCode("BILL123");
        existingTreasuryIuf2.setBillYear("2025");
        Mockito.when(treasuryService.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf2);

        TreasuryIufIngestionFlowFileResult result2 = service.processTreasuryCsv(
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
