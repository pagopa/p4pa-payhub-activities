package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.InvalidFlowDataException;
import it.gov.pagopa.payhub.activities.service.FlowValidatorService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingIngestionFlowFileActivityImplTest {
	private static final String FLOW_FILE_TYPE = "R";
	@Mock
	private IngestionFlowFileDao ingestionFlowFileDaoMock;
	@Mock
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
	@Mock
	private FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerServiceMock;
	@Mock
	private FlowValidatorService flowValidatorServiceMock;
	@Mock
	private PaymentsReportingMapperService paymentsReportingMapperServiceMock;
	private PaymentsReportingIngestionFlowFileActivityImpl ingestionActivity;

	@TempDir
	private File tempDir;
	private CtFlussoRiversamento ctFlussoRiversamento;


	@BeforeEach
	void setUp() {
		ingestionActivity = new PaymentsReportingIngestionFlowFileActivityImpl(
			FLOW_FILE_TYPE,
			ingestionFlowFileDaoMock,
			ingestionFlowFileRetrieverServiceMock,
			flussoRiversamentoUnmarshallerServiceMock,
			flowValidatorServiceMock,
			paymentsReportingMapperServiceMock
		);

		CtIdentificativoUnivocoPersonaG ctIdentificativoUnivocoPersonaG = new CtIdentificativoUnivocoPersonaG();
		ctIdentificativoUnivocoPersonaG.setCodiceIdentificativoUnivoco("80010020011");
		CtIstitutoRicevente istitutoRicevente = new CtIstitutoRicevente();
		istitutoRicevente.setIdentificativoUnivocoRicevente(ctIdentificativoUnivocoPersonaG);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIstitutoRicevente(istitutoRicevente);
		}

	@Test
	void givenSuccessfullConditionsWhenProcessFileThenOk() throws IOException {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		File file = new File(tempDir, "testFlussoRiversamento.xml");
		List<Path> mockedListPath = List.of(file.toPath());
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		List<PaymentsReportingDTO> dtoList = List.of(PaymentsReportingDTO.builder().flowIdentifierCode("idFlow").build());

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(List.of(ctFlussoRiversamento.getIdentificativoFlusso()), true);

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(file)).thenReturn(ctFlussoRiversamento);

		doNothing().when(flowValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);
		when(paymentsReportingMapperServiceMock.mapToDtoList(ctFlussoRiversamento, mockFlowDTO)).thenReturn(dtoList);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenNotExistentIngestionFlowFileWhenProcessFileThenFails() {
		// Given
		long ingestionFlowFileId = 123L;
		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenThrow(new RuntimeException("Flow retriever failed"));

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileDaoMock, times(1)).findById(ingestionFlowFileId);
		verifyNoInteractions(ingestionFlowFileRetrieverServiceMock);
	}

	@Test
	void givenIngestionFlowFileRetrieverServiceExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.flowFileType(FLOW_FILE_TYPE)
			.build();

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));

		doThrow(new RuntimeException("Setup process failed"))
			.when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileDaoMock, times(1)).findById(ingestionFlowFileId);
		verify(ingestionFlowFileRetrieverServiceMock, times(1))
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}

	@Test
	void givenUnmarshallingExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		File file = new File(tempDir, "testFlussoRiversamento.xml");
		List<Path> mockedListPath = List.of(file.toPath());
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false);

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(file)).thenThrow(ActivitiesException.class);
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenWrongTypeIngestionFlowFileWhenProcessFileThenFails() {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.flowFileType("E")
			.build();

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
	}

	@Test
	void givenValidationExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName("/valid/path")
			.flowFileType(FLOW_FILE_TYPE)
			.org(OrganizationDTO.builder().orgFiscalCode("0").build())
			.build();
		File file = new File(tempDir, "testFlussoRiversamento.xml");
		List<Path> mockedListPath = List.of(file.toPath());
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false);

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(file)).thenReturn(ctFlussoRiversamento);
		doThrow(new InvalidFlowDataException("invalid"))
			.when(flowValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}
}
