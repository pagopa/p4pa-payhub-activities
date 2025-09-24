package it.gov.pagopa.payhub.activities.service.files.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.treasury.TreasuryXlsInvalidFileException;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsServiceTest {

	private final TreasuryXlsServiceImpl sut = new TreasuryXlsServiceImpl();

	@Test
	void givenFileWithNoErrorWhenReadXlsThenAllRowsHaveBeenProcessedSuccessfully() {
		//GIVEN
		Map<String, String> iuf2TreasuryIdMap = new HashMap<>();
		AtomicInteger errorCount = new AtomicInteger(0);
		//WHEN
		TreasuryIufIngestionFlowFileResult result = sut.readXls(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_1_XLS_ALL_RECORDS_OK.xls"),
				iter -> {
					TreasuryIufIngestionFlowFileResult res = new TreasuryIufIngestionFlowFileResult();
					while(iter.hasNext()) {
						try {
							TreasuryXlsIngestionFlowFileDTO next = iter.next();
							String iuf = TreasuryUtils.getIdentificativo(next.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
							iuf2TreasuryIdMap.put(iuf, null);
						} catch (Exception e){
							errorCount.incrementAndGet();
						}
					}
					res.setTotalRows(iuf2TreasuryIdMap.size());
					res.setIuf2TreasuryIdMap(iuf2TreasuryIdMap);
					return res;
				}
		);
		//THEN
		Assertions.assertEquals(5,result.getIuf2TreasuryIdMap().size());
		Assertions.assertEquals(0, errorCount.get());
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072600"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072601"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072602"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072603"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072604"));
	}

	@Test
	void givenFileWithSomeErrorWhenReadXlsThenNotAllRowsHaveBeenProcessedSuccessfully() {
		//GIVEN
		Map<String, String> iuf2TreasuryIdMap = new HashMap<>();
		AtomicInteger errorCount = new AtomicInteger(0);
		List<Exception> errors = new ArrayList<>();
		//WHEN
		TreasuryIufIngestionFlowFileResult result = sut.readXls(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_2_XLS_WITH_ERRORS.xls"),
				iter -> {
					TreasuryIufIngestionFlowFileResult res = new TreasuryIufIngestionFlowFileResult();
					while(iter.hasNext()) {
						try {
							TreasuryXlsIngestionFlowFileDTO next = iter.next();
							String iuf = TreasuryUtils.getIdentificativo(next.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
							iuf2TreasuryIdMap.put(iuf, null);
						} catch (Exception e){
							errorCount.incrementAndGet();
							errors.add(e);
						}
					}
					res.setTotalRows(iuf2TreasuryIdMap.size());
					res.setIuf2TreasuryIdMap(iuf2TreasuryIdMap);
					return res;
				}
		);
		//THEN
		Assertions.assertEquals(1,result.getIuf2TreasuryIdMap().size());
		Assertions.assertEquals(5, errorCount.get());
		Assertions.assertEquals(5, errors.size());
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072604"));
		Assertions.assertEquals(IllegalStateException.class, errors.get(0).getClass());
		Assertions.assertEquals("Xls Cell with name \"%s\" must not be null or blank".formatted(TreasuryXlsHeadersEnum.DATA_CONTABILE.getValue()), errors.get(0).getMessage());
		Assertions.assertEquals(IllegalStateException.class, errors.get(1).getClass());
		Assertions.assertEquals("Error in parsing Long from value \"amount\" for Xls cell \"%s\"".formatted(TreasuryXlsHeadersEnum.IMPORTO.getValue()), errors.get(1).getMessage());
		Assertions.assertEquals(IllegalStateException.class, errors.get(2).getClass());
		Assertions.assertEquals("Error in parsing LocalDate from value \"data\" for Xls cell \"%s\"".formatted(TreasuryXlsHeadersEnum.DATA_VALUTA.getValue()), errors.get(2).getMessage());
		Assertions.assertEquals(IllegalStateException.class, errors.get(3).getClass());
		Assertions.assertEquals("Xls Cell with name \"%s\" must not be null or blank".formatted(TreasuryXlsHeadersEnum.DESCRIZIONE_ESTESA.getValue()), errors.get(3).getMessage());
		Assertions.assertEquals(IllegalStateException.class, errors.get(4).getClass());
		Assertions.assertEquals("Xls Cell with name \"%s\" must not be null or blank".formatted(TreasuryXlsHeadersEnum.IMPORTO.getValue()), errors.get(4).getMessage());
	}

	@Test
	void givenFileWithSwappedHeadersWhenReadXlsThenAllRowsHaveBeenProcessedSuccessfully() {
		//GIVEN
		Map<String, String> iuf2TreasuryIdMap = new HashMap<>();
		AtomicInteger errorCount = new AtomicInteger(0);
		//WHEN
		TreasuryIufIngestionFlowFileResult result = sut.readXls(
				Path.of("src/test/resources/treasury/xls/IPA_TEST_3_XLS_WITH_SWAPPED_HEADERS.xls"),
				iter -> {
					TreasuryIufIngestionFlowFileResult res = new TreasuryIufIngestionFlowFileResult();
					while(iter.hasNext()) {
						try {
							TreasuryXlsIngestionFlowFileDTO next = iter.next();
							String iuf = TreasuryUtils.getIdentificativo(next.getExtendedRemittanceDescription(), TreasuryUtils.IUF);
							iuf2TreasuryIdMap.put(iuf, null);
						} catch (Exception e){
							errorCount.incrementAndGet();
						}
					}
					res.setTotalRows(iuf2TreasuryIdMap.size());
					res.setIuf2TreasuryIdMap(iuf2TreasuryIdMap);
					return res;
				}
		);
		//THEN
		Assertions.assertEquals(5,result.getIuf2TreasuryIdMap().size());
		Assertions.assertEquals(0, errorCount.get());
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072600"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072601"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072602"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072603"));
		Assertions.assertTrue(iuf2TreasuryIdMap.containsKey("2024-07-26PPAYITR1XXX-S2024072604"));
	}

	@Test
	void givenInvalidFileWhenReadXlsThenThrowTreasuryXlsInvalidFileException() {
		//GIVEN
		Path path = Path.of("src/test/resources/treasury/xls/invalid_file.xls");
		//WHEN
		TreasuryXlsInvalidFileException ex = Assertions.assertThrows(
				TreasuryXlsInvalidFileException.class,
				() -> sut.readXls(
						path,
						iter -> new TreasuryIufIngestionFlowFileResult()
				)
		);
		//THEN
		Assertions.assertEquals("Cannot parse treasury Xls file \"invalid_file.xls\"", ex.getMessage());
	}

	@Test
	void givenEmptyFileWhenReadXlsThenThrowTreasuryXlsInvalidFileException() throws IOException {
		//GIVEN
		Path workingDirectory = Path.of("build", "test");
		Files.createDirectories(workingDirectory);
		Path emptyFile = Files.createTempFile(workingDirectory, "empty", ".xls");
		try {
			//WHEN
			TreasuryXlsInvalidFileException ex = Assertions.assertThrows(TreasuryXlsInvalidFileException.class, () -> sut.readXls(
					emptyFile,
					iter -> null
			));
			//THEN
			Assertions.assertEquals("Cannot parse treasury Xls file \"%s\"".formatted(emptyFile.getFileName()), ex.getMessage());
		} finally {
			Files.delete(emptyFile);
		}
	}
}