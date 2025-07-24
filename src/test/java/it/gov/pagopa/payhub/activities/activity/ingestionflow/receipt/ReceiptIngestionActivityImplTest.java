package it.gov.pagopa.payhub.activities.activity.ingestionflow.receipt;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.CsvService;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptProcessingService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceiptIngestionActivityImplTest {
	@Mock
	private CsvService csvServiceMock;
	@Mock
	private ReceiptProcessingService receiptProcessingServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;
	@Mock
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
	@Mock
	private FileArchiverService fileArchiverServiceMock;

	private ReceiptIngestionActivity activity;

	@BeforeEach
	void setUp() {
		activity = new ReceiptIngestionActivityImpl(
				ingestionFlowFileServiceMock,
				ingestionFlowFileRetrieverServiceMock,
				fileArchiverServiceMock,
				csvServiceMock,
				receiptProcessingServiceMock
		);
	}

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
				ingestionFlowFileServiceMock,
				ingestionFlowFileRetrieverServiceMock,
				fileArchiverServiceMock,
				csvServiceMock,
				receiptProcessingServiceMock
		);
	}

	@TempDir
	Path workingDir;

	@Test
	void givenValidIngestionFlowWhenProcessFileThenOk() throws IOException {
		// Given
		Long ingestionFlowFileId = 1L;
		IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
		ingestionFlowFileDTO.setFilePathName(workingDir.toString());
		ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT);
		Iterator<ReceiptIngestionFlowFileDTO> iterator = buildReceiptIngestionFlowFileDTO();
		List<CsvException> readerExceptions = List.of();
		ReceiptIngestionFlowFileResult expectedResult = TestUtils.getPodamFactory().manufacturePojo(ReceiptIngestionFlowFileResult.class);

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);

		Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
				.thenReturn(Optional.of(ingestionFlowFileDTO));

		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(ReceiptIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
				.thenAnswer(invocation -> {
					BiFunction<Iterator<ReceiptIngestionFlowFileDTO>, List<CsvException>, ReceiptIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
					return rowProcessor.apply(iterator, readerExceptions);
				});

		Mockito.when(receiptProcessingServiceMock.processReceipts(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
				.thenReturn(expectedResult);

		// When
		ReceiptIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

		// Then
		Assertions.assertSame(expectedResult, result);
		Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
		Assertions.assertFalse(filePath.toFile().exists());
	}

	@Test
	void givenValidIngestionFlowWhenExceptionThenThrowInvalidIngestionFileException() throws IOException {
		// Given
		Long ingestionFlowFileId = 1L;
		IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
		ingestionFlowFileDTO.setFilePathName(workingDir.toString());
		ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT);
		Iterator<ReceiptIngestionFlowFileDTO> iterator = buildReceiptIngestionFlowFileDTO();
		List<CsvException> readerExceptions = List.of();

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);

		Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
				.thenReturn(Optional.of(ingestionFlowFileDTO));

		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		Mockito.when(csvServiceMock.readCsv(eq(filePath), eq(ReceiptIngestionFlowFileDTO.class), any(), eq(ingestionFlowFileDTO.getFileVersion())))
				.thenAnswer(invocation -> {
					BiFunction<Iterator<ReceiptIngestionFlowFileDTO>, List<CsvException>, ReceiptIngestionFlowFileResult> rowProcessor = invocation.getArgument(2);
					return rowProcessor.apply(iterator, readerExceptions);
				});

		Mockito.when(receiptProcessingServiceMock.processReceipts(same(iterator), same(readerExceptions), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
				.thenThrow(new RestClientException("Error"));

		// When & Then
		assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
	}

	private Iterator<ReceiptIngestionFlowFileDTO> buildReceiptIngestionFlowFileDTO() {
		List<ReceiptIngestionFlowFileDTO> receiptIngestionFlowFileDTOList = List.of(
				ReceiptIngestionFlowFileDTO.builder()
						.sourceFlowName("iuf1")
						.paymentAmountCents(BigDecimal.valueOf(1L))
						.build(),
				ReceiptIngestionFlowFileDTO.builder()
						.sourceFlowName("iuf2")
						.paymentAmountCents(BigDecimal.valueOf(2L))
						.build()
		);

		return receiptIngestionFlowFileDTOList.iterator();
	}
}
