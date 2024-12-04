package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.service.JaxbTrasformerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentsReportingIngestionFlowFileActivityImplTest {
	private Resource resource;
	private IngestionFlowFileDao ingestionFlowFileDao;
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverService;
	private JaxbTrasformerService jaxbTrasformerService;
	private PaymentsReportingIngestionFlowFileActivityImpl ingestionActivity;

	@TempDir
	File tempDir;


	@BeforeEach
	void setUp() {
		ingestionFlowFileDao = mock(IngestionFlowFileDao.class);
		ingestionFlowFileRetrieverService = mock(IngestionFlowFileRetrieverService.class);
		jaxbTrasformerService = mock(JaxbTrasformerService.class);
		resource = mock(Resource.class);
		ingestionActivity = new PaymentsReportingIngestionFlowFileActivityImpl(
			resource,
			ingestionFlowFileDao,
			ingestionFlowFileRetrieverService,
			jaxbTrasformerService
		);
	}

	@Test
	void processFile_SuccessfulFlow() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.build();
		File file = new File(tempDir, "testFlussoRiversamento.xml");
		List<Path> mockedListPath = List.of(file.toPath());
		CtFlussoRiversamento ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(List.of(ctFlussoRiversamento.getIdentificativoFlusso()), true);

		when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverService)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(jaxbTrasformerService.unmarshaller(file, CtFlussoRiversamento.class, resource.getURL()))
			.thenReturn(ctFlussoRiversamento);
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void processFile_FlowRetrieverFails() {
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
	void processFile_RetrieveFileFails() throws Exception {
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

	@Test
	void processFile_UnmarshalFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.build();
		File file = new File(tempDir, "testFlussoRiversamento.xml");
		List<Path> mockedListPath = List.of(file.toPath());
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false);

		when(ingestionFlowFileDao.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverService)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(jaxbTrasformerService.unmarshaller(file, CtFlussoRiversamento.class, resource.getURL()))
			.thenThrow(ActivitiesException.class);
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}
}
