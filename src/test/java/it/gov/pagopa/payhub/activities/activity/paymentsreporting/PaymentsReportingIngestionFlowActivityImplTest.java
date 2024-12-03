package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowActivityResult;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentsReportingIngestionFlowActivityImplTest {

	private IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private IngestionFileRetrieverService ingestionFileRetrieverService;
	private PaymentsReportingIngestionFlowActivityImpl ingestionActivity;

	@BeforeEach
	void setUp() {
		ingestionFlowRetrieverService = mock(IngestionFlowRetrieverService.class);
		ingestionFileRetrieverService = mock(IngestionFileRetrieverService.class);

		ingestionActivity = new PaymentsReportingIngestionFlowActivityImpl(
			ingestionFlowRetrieverService,
			ingestionFileRetrieverService
		);
	}

	@Test
	void retrieveFile_SuccessfulFlow() throws Exception {
		// Given
		long ingestionFlowId = 123L;
		IngestionFlowFileDTO mockFlowDTO = new IngestionFlowFileDTO();
		mockFlowDTO.setFilePathName("/valid/path");
		mockFlowDTO.setFileName("valid-file.zip");
		mockFlowDTO.setRequestTokenCode("valid-token");

		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenReturn(mockFlowDTO);

		// When
		PaymentsReportingIngestionFlowActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertTrue(result.isSuccess());
		assertNotNull(result.getIufs());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verify(ingestionFileRetrieverService, times(1))
			.retrieveFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}

	@Test
	void retrieveFile_FlowRetrieverFails() {
		// Given
		long ingestionFlowId = 123L;
		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenThrow(new RuntimeException("Flow retriever failed"));

		// When
		PaymentsReportingIngestionFlowActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verifyNoInteractions(ingestionFileRetrieverService);
	}

	@Test
	void retrieveFile_RetrieveFileFails() throws Exception {
		// Given
		long ingestionFlowId = 123L;
		IngestionFlowFileDTO mockFlowDTO = new IngestionFlowFileDTO();
		mockFlowDTO.setFilePathName("/valid/path");
		mockFlowDTO.setFileName("valid-file.zip");
		mockFlowDTO.setRequestTokenCode("valid-token");

		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenReturn(mockFlowDTO);

		doThrow(new RuntimeException("Setup process failed"))
			.when(ingestionFileRetrieverService)
			.retrieveFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());

		// When
		PaymentsReportingIngestionFlowActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verify(ingestionFileRetrieverService, times(1))
			.retrieveFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}
}
