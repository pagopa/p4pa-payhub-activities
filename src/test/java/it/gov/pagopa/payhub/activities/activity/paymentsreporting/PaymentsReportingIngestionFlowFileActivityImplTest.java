package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.TransferSemanticKeyDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.ActivitiesException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFlowFileDataException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
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
	private static final IngestionFlowFile.FlowFileTypeEnum FLOW_FILE_TYPE = IngestionFlowFile.FlowFileTypeEnum.PAYMENTS_REPORTING;
	@Mock
	private IngestionFlowFileService ingestionFlowFileServiceMock;
	@Mock
	private IngestionFlowFileRetrieverService ingestionFlowFileRetrieverServiceMock;
	@Mock
	private FlussoRiversamentoUnmarshallerService flussoRiversamentoUnmarshallerServiceMock;
	@Mock
	private PaymentsReportingIngestionFlowFileValidatorService paymentsReportingIngestionFlowFileValidatorServiceMock;
	@Mock
	private PaymentsReportingMapperService paymentsReportingMapperServiceMock;
	@Mock
	private PaymentsReportingService paymentsReportingServiceMock;
	@Mock
	private IngestionFlowFileArchiverService ingestionFlowFileArchiverServiceMock;

	private PaymentsReportingIngestionFlowFileActivityImpl ingestionActivity;

	private CtFlussoRiversamento ctFlussoRiversamento;

	@TempDir
	private Path workingDir;

	@BeforeEach
	void setUp() {
		ingestionActivity = new PaymentsReportingIngestionFlowFileActivityImpl(
				ingestionFlowFileServiceMock,
			ingestionFlowFileRetrieverServiceMock,
			flussoRiversamentoUnmarshallerServiceMock,
			paymentsReportingIngestionFlowFileValidatorServiceMock,
			paymentsReportingMapperServiceMock,
			paymentsReportingServiceMock,
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
		IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L)
			.build();
		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReporting paymentsReportingDTO = PaymentsReporting.builder().iuf("idFlow").organizationId(1L).iuv("iuv").iur("iur").transferIndex(1).build();
		List<PaymentsReporting> dtoList = List.of(paymentsReportingDTO);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder().orgId(1L).iuv("iuv").iur("iur").transferIndex(1).build();

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(List.of(transferSemanticKeyDTO), true, null);

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateData(ctFlussoRiversamento, ingestionFlowFileDTO);
		when(paymentsReportingMapperServiceMock.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO)).thenReturn(dtoList);
		doReturn(1).when(paymentsReportingServiceMock).saveAll(dtoList);
		when(paymentsReportingMapperServiceMock.map2TransferSemanticKeyDto(paymentsReportingDTO)).thenReturn(transferSemanticKeyDTO);
		doNothing().when(ingestionFlowFileArchiverServiceMock)
			.archive(ingestionFlowFileDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenNotExistentIngestionFlowFileWhenProcessFileThenFails() {
		// Given
		long ingestionFlowFileId = 123L;
		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenThrow(new RuntimeException("Flow retriever failed"));

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileServiceMock, times(1)).findById(ingestionFlowFileId);
		verifyNoInteractions(ingestionFlowFileRetrieverServiceMock);
	}

	@Test
	void givenIngestionFlowFileRetrieverServiceExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(123L)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(99L)
			.build();

		when(ingestionFlowFileServiceMock.findById(mockFlowDTO.getIngestionFlowFileId())).thenReturn(Optional.of(mockFlowDTO));
		doThrow(new RuntimeException("Setup process failed"))
				.when(ingestionFlowFileRetrieverServiceMock)
				.retrieveAndUnzipFile(mockFlowDTO.getOrganizationId(), Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(mockFlowDTO.getIngestionFlowFileId());

		// Then
		assertFalse(result.isSuccess());
		verify(ingestionFlowFileRetrieverServiceMock, times(1))
				.retrieveAndUnzipFile(mockFlowDTO.getOrganizationId(), Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
	}

	@Test
	void givenUnmarshallingExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "error occured");

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(mockFlowDTO.getOrganizationId(), Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
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
		IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.flowFileType(IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI)
			.build();

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertFalse(result.isSuccess());
	}

	@Test
	void givenValidationExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "invalid");

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(mockFlowDTO.getOrganizationId(), Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);
		doThrow(new InvalidIngestionFlowFileDataException("invalid"))
			.when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateData(ctFlussoRiversamento, mockFlowDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenPaymentsReportingExceptionWhenProcessFileThenFails() throws Exception {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile mockFlowDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L)
			.build();
		Path filePath = Files.createFile(Path.of(mockFlowDTO.getFilePathName()).resolve(mockFlowDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		List<PaymentsReporting> dtoList = List.of(PaymentsReporting.builder().iuf("idFlow").build());

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "saving fails");

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(mockFlowDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(mockFlowDTO.getOrganizationId(), Path.of(mockFlowDTO.getFilePathName()), mockFlowDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateData(ctFlussoRiversamento, mockFlowDTO);
		when(paymentsReportingMapperServiceMock.map2PaymentsReportings(ctFlussoRiversamento, mockFlowDTO)).thenReturn(dtoList);
		doThrow(new ActivitiesException("saving fails"))
			.when(paymentsReportingServiceMock).saveAll(dtoList);
		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}

	@Test
	void givenIOExceptionWhenProcessFileThenFails() throws IOException {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFile.builder()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L)
			.build();
		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento = new CtFlussoRiversamento();
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReporting paymentsReportingDTO = PaymentsReporting.builder().iuf("idFlow").organizationId(1L).iuv("iuv").iur("iur").transferIndex(1).build();
		List<PaymentsReporting> dtoList = List.of(paymentsReportingDTO);
		TransferSemanticKeyDTO transferSemanticKeyDTO = TransferSemanticKeyDTO.builder().orgId(1L).iuv("iuv").iur("iur").transferIndex(1).build();

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult(Collections.emptyList(), false, "error occured");

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateData(ctFlussoRiversamento, ingestionFlowFileDTO);
		when(paymentsReportingMapperServiceMock.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO)).thenReturn(dtoList);
		doReturn(1).when(paymentsReportingServiceMock).saveAll(dtoList);
		when(paymentsReportingMapperServiceMock.map2TransferSemanticKeyDto(paymentsReportingDTO)).thenReturn(transferSemanticKeyDTO);
		doThrow(new IOException("error occured")).when(ingestionFlowFileArchiverServiceMock)
			.archive(ingestionFlowFileDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);
	}
}
