package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.pagopa.payhub.activities.service.IngestionFileHandlerService;
import it.gov.pagopa.payhub.activities.service.IngestionFileValidatorService;
import it.gov.pagopa.payhub.activities.service.IngestionFlowRetrieverService;
import it.gov.pagopa.payhub.activities.dto.reportingflow.ReportingFlowIngestionActivityResult;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.exception.InvalidIngestionFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportingFlowIngestionActivityImplTest {

	private IngestionFlowRetrieverService ingestionFlowRetrieverService;
	private IngestionFileValidatorService ingestionFileValidatorService;
	private IngestionFileHandlerService ingestionFileHandlerService;
	private ReportingFlowIngestionActivityImpl ingestionActivity;

	@BeforeEach
	void setUp() {
		ingestionFlowRetrieverService = mock(IngestionFlowRetrieverService.class);
		ingestionFileValidatorService = mock(IngestionFileValidatorService.class);
		ingestionFileHandlerService = mock(IngestionFileHandlerService.class);

		ingestionActivity = new ReportingFlowIngestionActivityImpl(
			ingestionFlowRetrieverService,
			ingestionFileValidatorService,
			ingestionFileHandlerService
		);
	}

	@Test
	void processFile_SuccessfulFlow() throws Exception {
		// Given
		String ingestionFlowId = "123";
		IngestionFlowDTO mockFlowDTO = new IngestionFlowDTO();
		mockFlowDTO.setFilePathName("/valid/path");
		mockFlowDTO.setFileName("valid-file.zip");
		mockFlowDTO.setRequestTokenCode("valid-token");

		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenReturn(mockFlowDTO);

		// When
		ReportingFlowIngestionActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertTrue(result.isSuccess());
		assertNotNull(result.getIufs());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verify(ingestionFileValidatorService, times(1))
			.validate(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName(), mockFlowDTO.getRequestTokenCode());
		verify(ingestionFileHandlerService, times(1))
			.setUpProcess(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName());
	}

	@Test
	void processFile_FlowRetrieverFails() {
		// Given
		String ingestionFlowId = "123";
		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenThrow(new RuntimeException("Flow retriever failed"));

		// When
		ReportingFlowIngestionActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verifyNoInteractions(ingestionFileValidatorService);
		verifyNoInteractions(ingestionFileHandlerService);
	}

	@Test
	void processFile_ValidationFails() throws Exception {
		// Given
		String ingestionFlowId = "123";
		IngestionFlowDTO mockFlowDTO = new IngestionFlowDTO();
		mockFlowDTO.setFilePathName("/valid/path");
		mockFlowDTO.setFileName("valid-file.zip");
		mockFlowDTO.setRequestTokenCode("valid-token");

		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenReturn(mockFlowDTO);

		doThrow(new InvalidIngestionFileException("Validation failed"))
			.when(ingestionFileValidatorService)
			.validate(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName(), mockFlowDTO.getRequestTokenCode());

		// When
		ReportingFlowIngestionActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verify(ingestionFileValidatorService, times(1))
			.validate(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName(), mockFlowDTO.getRequestTokenCode());
		verifyNoInteractions(ingestionFileHandlerService);
	}

	@Test
	void processFile_SetupProcessFails() throws Exception {
		// Given
		String ingestionFlowId = "123";
		IngestionFlowDTO mockFlowDTO = new IngestionFlowDTO();
		mockFlowDTO.setFilePathName("/valid/path");
		mockFlowDTO.setFileName("valid-file.zip");
		mockFlowDTO.setRequestTokenCode("valid-token");

		when(ingestionFlowRetrieverService.getIngestionFlow(Long.valueOf(ingestionFlowId)))
			.thenReturn(mockFlowDTO);

		doThrow(new RuntimeException("Setup process failed"))
			.when(ingestionFileHandlerService)
			.setUpProcess(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName());

		// When
		ReportingFlowIngestionActivityResult result = ingestionActivity.processFile(ingestionFlowId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowRetrieverService, times(1))
			.getIngestionFlow(Long.valueOf(ingestionFlowId));
		verify(ingestionFileValidatorService, times(1))
			.validate(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName(), mockFlowDTO.getRequestTokenCode());
		verify(ingestionFileHandlerService, times(1))
			.setUpProcess(mockFlowDTO.getFilePathName(), mockFlowDTO.getFileName());
	}
}
