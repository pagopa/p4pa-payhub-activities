package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.dao.IngestionFlowFileDao;
import it.gov.pagopa.payhub.activities.dao.PaymentsReportingDao;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.enums.IngestionFlowFileType;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFlowFileDataException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsReportingIngestionFlowFileActivityImplTest {
	private static final IngestionFlowFileType FLOW_FILE_TYPE = IngestionFlowFileType.PAYMENTS_REPORTING;
    private static final String TARGET_DIR = "/target/";
	@Mock
	private IngestionFlowFileDao ingestionFlowFileDaoMock;
	@Mock
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
	@Mock
	private FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerServiceMock;
	@Mock
	private PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorServiceMock;
	@Mock
	private PaymentsReportingMapperService paymentsReportingMapperServiceMock;
	@Mock
	private PaymentsReportingDao paymentsReportingDaoMock;
	@Mock
	private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;

	private PaymentsReportingIngestionFlowFileActivityImpl ingestionActivity;

	private CtFlussoRiversamento ctFlussoRiversamento;

	@TempDir
	Path workingDir;

	@BeforeEach
	void setUp() {
		ingestionActivity = new PaymentsReportingIngestionFlowFileActivityImpl(
			TARGET_DIR,
			ingestionFlowFileDaoMock,
			ingestionFlowFileRetrieverServiceMock,
			flussoRiversamentoUnmarshallerServiceMock,
			paymentsReportingIngestionFlowFileValidatorServiceMock,
			paymentsReportingMapperServiceMock,
			paymentsReportingDaoMock,
			ingestionFlowFileArchiverServiceMock
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
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		List<PaymentsReportingDTO> dtoList = List.of(PaymentsReportingDTO.builder().iuf("idFlow").build());

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(List.of(ctFlussoRiversamento.getIdentificativoFlusso()), true, null);

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);
		when(paymentsReportingMapperServiceMock.mapToDtoList(ctFlussoRiversamento, mockFlowDTO)).thenReturn(dtoList);
		doReturn(dtoList).when(paymentsReportingDaoMock).saveAll(dtoList);
		doNothing().when(ingestionFlowFileArchiverServiceMock)
			.archive(mockedListPath, Path.of(mockFlowDTO.getFilePathName(), TARGET_DIR));

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
			.filePathName(workingDir.toString())
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
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "error occured");

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenThrow(new ActivitiesException("error occured"));
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
			.flowFileType(IngestionFlowFileType.OPI)
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
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.org(OrganizationDTO.builder().orgFiscalCode("0").build())
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "invalid");

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);
		doThrow(new InvalidIngestionFlowFileDataException("invalid"))
			.when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenPaymentsReportingExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		List<PaymentsReportingDTO> dtoList = List.of(PaymentsReportingDTO.builder().iuf("idFlow").build());

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "saving fails");

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);
		when(paymentsReportingMapperServiceMock.mapToDtoList(ctFlussoRiversamento, mockFlowDTO)).thenReturn(dtoList);
		doThrow(new ActivitiesException("saving fails"))
			.when(paymentsReportingDaoMock).saveAll(dtoList);
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenIOExceptionWhenProcessFileThenFails() throws IOException {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFileDTO mockFlowDTO = IngestionFlowFileDTO.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		List<PaymentsReportingDTO> dtoList = List.of(PaymentsReportingDTO.builder().iuf("idFlow").build());

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "error occured");

		when(ingestionFlowFileDaoMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateOrganization(ctFlussoRiversamento, mockFlowDTO);
		when(paymentsReportingMapperServiceMock.mapToDtoList(ctFlussoRiversamento, mockFlowDTO)).thenReturn(dtoList);
		doReturn(dtoList).when(paymentsReportingDaoMock).saveAll(dtoList);

		doThrow(new IOException("error occured")).when(ingestionFlowFileArchiverServiceMock)
			.archive(mockedListPath, Path.of(mockFlowDTO.getFilePathName(), TARGET_DIR));

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}
}
