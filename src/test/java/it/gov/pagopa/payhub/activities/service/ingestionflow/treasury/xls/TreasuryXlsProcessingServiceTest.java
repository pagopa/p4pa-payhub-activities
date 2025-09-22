package it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.connector.classification.TreasuryService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.treasury.TreasuryIuf;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls.TreasuryXlsMapper;
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

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.TestUtils.*;
import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsProcessingServiceTest {
	private final PodamFactory podamFactory = TestUtils.getPodamFactory();

	@Mock
	private TreasuryXlsErrorsArchiverService errorsArchiverServiceMock;

	@Mock
	private OrganizationService organizationServiceMock;

	@Mock
	private Path workingDirectoryMock;

	@Mock
	private TreasuryXlsMapper mapperMock;

	@Mock
	private TreasuryService treasuryServiceMock;

	private TreasuryXlsProcessingService service;

	@BeforeEach
	void setUp() {
		service = new TreasuryXlsProcessingService(
				mapperMock,
				treasuryServiceMock,
				errorsArchiverServiceMock,
				organizationServiceMock
		);
	}

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
				errorsArchiverServiceMock,
				organizationServiceMock,
				workingDirectoryMock,
				mapperMock,
				treasuryServiceMock
		);
	}

	@Test
	void givenValidInputWhenProcessTreasuryXlsThenCompleteWithNoErrors() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		Long orgId = 1L;
		organization.setOrganizationId(orgId);
		Optional<Organization> organizationOptional = Optional.of(organization);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2025);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2025);

		Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

		Treasury treasury = podamFactory.manufacturePojo(Treasury.class);
		treasury.setIuf("IUF12345");
		treasury.setTreasuryId("TREASURY_ID_1");
		Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(treasury);
		Mockito.when(treasuryServiceMock.insert(treasury)).thenReturn(treasury);
		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(
				ingestionFlowFile.getOrganizationId(),
				TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)
		)).thenReturn(null);
		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				Stream.of(dto).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		Assertions.assertEquals(1L, result.getProcessedRows());
		Assertions.assertEquals(1L, result.getTotalRows());

		Mockito.verify(treasuryServiceMock).getByOrganizationIdAndIuf(
				ingestionFlowFile.getOrganizationId(),
				TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)
		);
		Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
		Mockito.verify(treasuryServiceMock).insert(treasury);
	}

	@Test
	void givenThrowExceptionWhenProcessTreasuryXlsThenAddError() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		Long orgId = 1L;
		organization.setOrganizationId(orgId);
		Optional<Organization> organizationOptional = Optional.of(organization);

		TreasuryXlsIngestionFlowFileDTO dto = TestUtils.getPodamFactory().manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2025);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2025);

		Mockito.when(organizationServiceMock.getOrganizationById(orgId)).thenReturn(organizationOptional);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();

		Treasury treasury = mock(Treasury.class);
		Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(treasury);
		Mockito.when(treasuryServiceMock.insert(treasury))
				.thenThrow(new RuntimeException("Processing error"));

		Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectoryMock, ingestionFlowFile))
				.thenReturn("zipFileName.csv");

		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)))
				.thenReturn(null);



		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				Stream.of(dto).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		assertEquals(1, result.getTotalRows());
		assertEquals(0, result.getProcessedRows());
		assertEquals("Some rows have failed", result.getErrorDescription());
		assertEquals("zipFileName.csv", result.getDiscardedFileName());
		Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
		Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());

		verify(mapperMock).map(dto, ingestionFlowFile);
		verify(treasuryServiceMock).insert(treasury);
		verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectoryMock), Mockito.eq(ingestionFlowFile), Mockito.anyList());
		verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectoryMock, ingestionFlowFile);
	}

	@Test
	void givenExistingTreasuryWhenProcessTreasuryXlsThenCompleteWithoutProcessing() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		organization.setOrganizationId(1L);
		Optional<Organization> organizationOptional = Optional.of(organization);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2025);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2025);

		Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

		String iuf = TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF);

		TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
		existingTreasuryIuf.setIuf(iuf);
		existingTreasuryIuf.setBillCode("XLS_" + iuf);
		existingTreasuryIuf.setBillYear("2020");
		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				Stream.of(dto).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		Assertions.assertEquals(0L, result.getProcessedRows());
		Assertions.assertEquals(1L, result.getTotalRows());
		Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
		Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
		Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
		Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());
		verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectoryMock), Mockito.eq(ingestionFlowFile), Mockito.anyList());
		verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectoryMock, ingestionFlowFile);
	}

	@Test
	void givenReaderExceptionWhenProcessTreasuryXlsThenCompleteWithNoErrors() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		organization.setOrganizationId(1L);
		Optional<Organization> organizationOptional = Optional.of(organization);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
		TreasuryXlsIngestionFlowFileDTO dto1 = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto1.setBillDate(LOCAL_DATE_2025);
		dto1.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		TreasuryXlsIngestionFlowFileDTO dto2 = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto2.setBillDate(LOCAL_DATE_2025);
		dto2.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011502");
		TreasuryXlsIngestionFlowFileDTO dto3 = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto3.setBillDate(LOCAL_DATE_2025);
		dto3.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011503");

		Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

		String iuf1 = TreasuryUtils.getIdentificativo(dto1.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
		String iuf2 = TreasuryUtils.getIdentificativo(dto2.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
		String iuf3 = TreasuryUtils.getIdentificativo(dto3.getExtendedRemittanceDescription(), TreasuryUtils.IUF);

		Treasury treasury1 = podamFactory.manufacturePojo(Treasury.class);
		treasury1.setIuf(iuf1);
		treasury1.setTreasuryId("TREASURY_ID_1");

		Treasury treasury2 = podamFactory.manufacturePojo(Treasury.class);
		treasury2.setIuf(iuf2);
		treasury2.setTreasuryId("TREASURY_ID_2");

		Treasury treasury3 = podamFactory.manufacturePojo(Treasury.class);
		treasury3.setIuf(iuf3);
		treasury3.setTreasuryId("TREASURY_ID_3");

		Mockito.when(
				treasuryServiceMock.getByOrganizationIdAndIuf(Mockito.eq(1L), Mockito.anyString())
				).thenReturn(null);

		Mockito.when(mapperMock.map(dto1, ingestionFlowFile)).thenReturn(treasury1);
		Mockito.when(treasuryServiceMock.insert(treasury1)).thenReturn(treasury1);
		Mockito.when(mapperMock.map(dto2, ingestionFlowFile)).thenReturn(treasury2);
		Mockito.when(treasuryServiceMock.insert(treasury2)).thenReturn(treasury2);
		Mockito.when(mapperMock.map(dto3, ingestionFlowFile)).thenReturn(treasury3);
		Mockito.when(treasuryServiceMock.insert(treasury3)).thenReturn(treasury3);

		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				new TestIterator(List.of(dto1,dto2,dto3)),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		Assertions.assertEquals(3L, result.getProcessedRows());
		Assertions.assertEquals(5L, result.getTotalRows());
		Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
		Assertions.assertEquals(3, result.getIuf2TreasuryIdMap().size());
		Mockito.verify(mapperMock, Mockito.times(3)).map(Mockito.any(), Mockito.any());
		Mockito.verify(treasuryServiceMock, Mockito.times(3)).insert(Mockito.any());
		verify(errorsArchiverServiceMock).writeErrors(Mockito.eq(workingDirectoryMock), Mockito.eq(ingestionFlowFile), Mockito.anyList());
		verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectoryMock, ingestionFlowFile);
	}

	@Test
	void givenExistingTreasuryWithSameBillCodeAndYearWhenProcessTreasuryXlsThenCompleteWithProcessing() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		organization.setOrganizationId(1L);
		Optional<Organization> organizationOptional = Optional.of(organization);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2025);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2025);

		Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

		String iuf = TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF);

		TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
		existingTreasuryIuf.setIuf(iuf);
		existingTreasuryIuf.setBillCode("XLS_" + iuf);
		existingTreasuryIuf.setBillYear("2025");
		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

		Treasury treasury = podamFactory.manufacturePojo(Treasury.class);
		treasury.setIuf(iuf);
		treasury.setTreasuryId("TREASURY_ID_1");
		Mockito.when(mapperMock.map(dto, ingestionFlowFile)).thenReturn(treasury);
		Mockito.when(treasuryServiceMock.insert(treasury)).thenReturn(treasury);

		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				Stream.of(dto).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		Assertions.assertEquals(1L, result.getProcessedRows());
		Assertions.assertEquals(1L, result.getTotalRows());
		Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
		Assertions.assertEquals(1, result.getIuf2TreasuryIdMap().size());
		Mockito.verify(mapperMock).map(dto, ingestionFlowFile);
		Mockito.verify(treasuryServiceMock).insert(treasury);
	}

	@Test
	void givenExistingTreasuryWithDifferentBillCodeOrYearWhenProcessTreasuryXlsThenCompleteWithoutProcessing() {
		String ipa = "IPA123";
		Organization organization = new Organization();
		organization.setIpaCode(ipa);
		organization.setOrganizationId(1L);
		Optional<Organization> organizationOptional = Optional.of(organization);

		IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
		TreasuryXlsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2025);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2025);

		Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(organizationOptional);

		String iuf = TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF);

		TreasuryIuf existingTreasuryIuf = new TreasuryIuf();
		existingTreasuryIuf.setIuf(iuf);
		existingTreasuryIuf.setBillCode("BILL999");
		existingTreasuryIuf.setBillYear("2020");
		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf);

		TreasuryIufIngestionFlowFileResult result = service.processTreasuryXls(
				Stream.of(dto).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
		Assertions.assertEquals(0L, result.getProcessedRows());
		Assertions.assertEquals(1L, result.getTotalRows());
		Assertions.assertNotNull(result.getIuf2TreasuryIdMap());
		Assertions.assertEquals(0, result.getIuf2TreasuryIdMap().size());
		Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
		Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());

		TreasuryXlsIngestionFlowFileDTO dto2 = podamFactory.manufacturePojo(TreasuryXlsIngestionFlowFileDTO.class);
		dto.setBillDate(LOCAL_DATE_2026);
		dto.setExtendedRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
		dto.setBillAmountCents(1235L);
		dto.setRegionValueDate(LOCAL_DATE_2026);

		TreasuryIuf existingTreasuryIuf2 = new TreasuryIuf();
		existingTreasuryIuf2.setIuf(iuf);
		existingTreasuryIuf2.setBillCode("BILL123");
		existingTreasuryIuf2.setBillYear("2025");
		Mockito.when(treasuryServiceMock.getByOrganizationIdAndIuf(1L, iuf)).thenReturn(existingTreasuryIuf2);

		TreasuryIufIngestionFlowFileResult result2 = service.processTreasuryXls(
				Stream.of(dto2).iterator(),
				ingestionFlowFile,
				workingDirectoryMock
		);

		Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result2.getOrganizationId());
		Assertions.assertEquals(0L, result2.getProcessedRows());
		Assertions.assertEquals(1L, result2.getTotalRows());
		Assertions.assertNotNull(result2.getIuf2TreasuryIdMap());
		Assertions.assertEquals(0, result2.getIuf2TreasuryIdMap().size());
		Mockito.verify(mapperMock, Mockito.never()).map(Mockito.any(), Mockito.any());
		Mockito.verify(treasuryServiceMock, Mockito.never()).insert(Mockito.any());
		verify(errorsArchiverServiceMock, Mockito.times(2)).writeErrors(Mockito.eq(workingDirectoryMock), Mockito.eq(ingestionFlowFile), Mockito.anyList());
		verify(errorsArchiverServiceMock, Mockito.times(2)).archiveErrorFiles(workingDirectoryMock, ingestionFlowFile);
	}

	private static class TestIterator implements Iterator<TreasuryXlsIngestionFlowFileDTO> {
		final int totRowCount;
		int rowCount = 0;
		final List<TreasuryXlsIngestionFlowFileDTO> dtos;

		private TestIterator(List<TreasuryXlsIngestionFlowFileDTO> dtos) {
			this.dtos = dtos;
			this.totRowCount = dtos.size() * 2 -1;
		}

		@Override
		public boolean hasNext() {
			return rowCount++ < totRowCount;
		}

		@Override
		public TreasuryXlsIngestionFlowFileDTO next() {
			if(rowCount % 2 == 0) {
				throw new RuntimeException("reader exception!");
			}
			return dtos.get((rowCount - 1) / 2);
		}
	}
}