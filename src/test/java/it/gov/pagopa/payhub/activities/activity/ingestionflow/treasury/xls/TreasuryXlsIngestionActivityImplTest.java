package it.gov.pagopa.payhub.activities.activity.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.TreasuryIufIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import it.gov.pagopa.payhub.activities.service.files.FileArchiverService;
import it.gov.pagopa.payhub.activities.service.files.xls.TreasuryXlsServiceImpl;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.xls.TreasuryXlsProcessingService;
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
import uk.co.jemos.podam.api.PodamFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class TreasuryXlsIngestionActivityImplTest {

	@Mock
	private TreasuryXlsServiceImpl xlsServiceMock;
	@Mock
	private TreasuryXlsProcessingService treasuryXlsProcessingServiceMock;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;
	@Mock
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
	@Mock
	private FileArchiverService fileArchiverServiceMock;

	private TreasuryXlsIngestionActivityImpl activity;

	@TempDir
	private Path workingDir;

	@BeforeEach
	void setUp() {
		activity = new TreasuryXlsIngestionActivityImpl(
				ingestionFlowFileServiceMock,
				ingestionFlowFileRetrieverServiceMock,
				fileArchiverServiceMock,
				treasuryXlsProcessingServiceMock,
				xlsServiceMock
		);
	}

	@AfterEach
	void verifyNoMoreInteractions() {
		Mockito.verifyNoMoreInteractions(
				ingestionFlowFileServiceMock,
				ingestionFlowFileRetrieverServiceMock,
				fileArchiverServiceMock,
				treasuryXlsProcessingServiceMock,
				xlsServiceMock
		);
	}

	private TreasuryIufIngestionFlowFileResult buildTreasuryIufIngestionFlowFileResult() {
		return TreasuryIufIngestionFlowFileResult.builder()
				.organizationId(10L)
				.processedRows(20L)
				.totalRows(30L)
				.discardedFileName("discardedFileName")
				.errorDescription("errorDescription")
				.build();
	}

	private final PodamFactory podamFactory = TestUtils.getPodamFactory();

	private Iterator<TreasuryXlsIngestionFlowFileDTO> buildTreasuryXlsIngestionFlowFileDTO() {
		List<TreasuryXlsIngestionFlowFileDTO> treasuryXlsIngestionFlowFileDTOs = List.of(
				podamFactory.manufacturePojo(
						TreasuryXlsIngestionFlowFileDTO.class),
				podamFactory.manufacturePojo(
						TreasuryXlsIngestionFlowFileDTO.class)
		);
		treasuryXlsIngestionFlowFileDTOs.forEach(x->x.setBillDate(LocalDate.of(2025,1,1)));

		return treasuryXlsIngestionFlowFileDTOs.iterator();
	}

	@Test
	void givenMultipleFilesWhenProcessingFileThenThrowsInvalidIngestionFileException() throws Exception {
		Long ingestionFlowFileId = 1L;
		Long organizationId = 10L;
		IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
		ingestionFlowFileDTO.setOrganizationId(organizationId);
		ingestionFlowFileDTO.setFilePathName(workingDir.toString());
		ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_XLS);

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

		List<Path> mockedListPath = List.of(filePath, filePath);

		Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
				.thenReturn(Optional.of(ingestionFlowFileDTO));

		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
	}

	@Test
	void givenValidFileWhenProcessingFileThenFileProcessedSuccessfullyAndArchived() throws Exception{
		Long ingestionFlowFileId = 1L;
		Long organizationId = 10L;
		IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
		ingestionFlowFileDTO.setOrganizationId(organizationId);
		ingestionFlowFileDTO.setFilePathName(workingDir.toString());
		ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_XLS);
		Iterator<TreasuryXlsIngestionFlowFileDTO> iterator = buildTreasuryXlsIngestionFlowFileDTO();

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));

		List<Path> mockedListPath = List.of(filePath);

		Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
				.thenReturn(Optional.of(ingestionFlowFileDTO));

		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		Mockito.when(xlsServiceMock.readXls(eq(filePath), any()))
				.thenAnswer(invocation -> {
					Function<Iterator<TreasuryXlsIngestionFlowFileDTO>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(1);
					return rowProcessor.apply(iterator);
				});

		Mockito.when(treasuryXlsProcessingServiceMock.processTreasuryXls(same(iterator), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
				.thenReturn(buildTreasuryIufIngestionFlowFileResult());

		TreasuryIufIngestionFlowFileResult result = activity.processFile(ingestionFlowFileId);

		Assertions.assertEquals(
				buildTreasuryIufIngestionFlowFileResult(),
				result);
		Mockito.verify(fileArchiverServiceMock, Mockito.times(1)).archive(ingestionFlowFileDTO);
		Assertions.assertFalse(filePath.toFile().exists());
	}

	@Test
	void givenValidIngestionFlowWhenProcessingThrowsExceptionThenThrowInvalidIngestionFileException() throws IOException {
		Long ingestionFlowFileId = 1L;
		Long organizationId = 10L;
		IngestionFlowFile ingestionFlowFileDTO = buildIngestionFlowFile();
		ingestionFlowFileDTO.setFilePathName(workingDir.toString());
		ingestionFlowFileDTO.setOrganizationId(organizationId);
		ingestionFlowFileDTO.setIngestionFlowFileType(IngestionFlowFile.IngestionFlowFileTypeEnum.TREASURY_XLS);
		Iterator<TreasuryXlsIngestionFlowFileDTO> iterator = buildTreasuryXlsIngestionFlowFileDTO();

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);

		Mockito.when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId))
				.thenReturn(Optional.of(ingestionFlowFileDTO));

		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());

		Mockito.when(xlsServiceMock.readXls(eq(filePath), any()))
				.thenAnswer(invocation -> {
					Function<Iterator<TreasuryXlsIngestionFlowFileDTO>, TreasuryIufIngestionFlowFileResult> rowProcessor = invocation.getArgument(1);
					return rowProcessor.apply(iterator);
				});

		Mockito.when(treasuryXlsProcessingServiceMock.processTreasuryXls(same(iterator), eq(ingestionFlowFileDTO), eq(filePath.getParent())))
				.thenThrow(new RestClientException("Error"));

		assertThrows(InvalidIngestionFileException.class, () -> activity.processFile(ingestionFlowFileId));
	}
}