package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentsReportingIngestionFlowFileActivityImplTest {

	private IngestionFlowFileDao ingestionFlowFileDao;
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private PaymentsReportingIngestionFlowFileActivityImpl ingestionActivity;

	@BeforeEach
	void setUp() {
		ingestionFlowFileDao = mock(IngestionFlowFileDao.class);
		ingestionFlowFileRetrieverService = mock(IngestionFlowFileRetrieverService.class);

		ingestionActivity = new PaymentsReportingIngestionFlowFileActivityImpl(
			ingestionFlowFileDao,
			ingestionFlowFileRetrieverService
		);
	}

	@Test
	void retrieveFile_SuccessfulFlow() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.requestTokenCode("valid-token")
			.build();

		when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertTrue(result.isSuccess());
		assertNotNull(result.getIufs());
		verify(ingestionFlowFileDao, times(1)).findById(ingestionFlowFileId);
		verify(ingestionFlowFileRetrieverService, times(1))
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}

	@Test
	void retrieveFile_FlowRetrieverFails() {
		// Given
		long ingestionFlowFileId = 123L;
		when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenThrow(new RuntimeException("Flow retriever failed"));

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileDao, times(1)).findById(ingestionFlowFileId);
		verifyNoInteractions(ingestionFlowFileRetrieverService);
	}

	@Test
	void retrieveFile_RetrieveFileFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.requestTokenCode("valid-token")
			.build();

		when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));

		doThrow(new RuntimeException("Setup process failed"))
			.when(ingestionFlowFileRetrieverService)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileDao, times(1)).findById(ingestionFlowFileId);
		verify(ingestionFlowFileRetrieverService, times(1))
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}
}
