package it.gov.pagopa.payhub.activities.activity.paymentsreporting;

import it.gov.digitpa.schemas._2011.pagamenti.CtFlussoRiversamento;
import it.gov.digitpa.schemas._2011.pagamenti.CtIdentificativoUnivocoPersonaG;
import it.gov.digitpa.schemas._2011.pagamenti.CtIstitutoRicevente;
import it.gov.pagopa.payhub.activities.connector.classification.PaymentsReportingService;
import it.gov.pagopa.payhub.activities.connector.processexecutions.IngestionFlowFileService;
import it.gov.pagopa.payhub.activities.dto.classifications.PaymentsReportingTransferDTO;
import it.gov.pagopa.payhub.activities.dto.paymentsreporting.PaymentsReportingIngestionFlowFileActivityResult;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowFileNotFoundException;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowFileRetrieverService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.FlussoRiversamentoUnmarshallerService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingIngestionFlowFileValidatorService;
import it.gov.pagopa.payhub.activities.service.paymentsreporting.PaymentsReportingMapperService;
import it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker;
import it.gov.pagopa.payhub.activities.util.faker.PaymentsReportingFaker;
import it.gov.pagopa.pu.classification.dto.generated.PaymentsReporting;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@AfterEach
	void verifyNoMoreInteractions(){
		Mockito.verifyNoMoreInteractions(
				ingestionFlowFileServiceMock,
				ingestionFlowFileRetrieverServiceMock,
				flussoRiversamentoUnmarshallerServiceMock,
				paymentsReportingIngestionFlowFileValidatorServiceMock,
				paymentsReportingMapperServiceMock,
				paymentsReportingServiceMock,
				ingestionFlowFileArchiverServiceMock
		);
	}

	@Test
	void givenSuccessfulConditionsWhenProcessFileThenOk() throws IOException {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile ingestionFlowFileDTO = IngestionFlowFileFaker.buildIngestionFlowFile()
			.ingestionFlowFileId(ingestionFlowFileId)
			.fileName("valid-file.zip")
			.filePathName(workingDir.toString())
			.flowFileType(FLOW_FILE_TYPE)
			.organizationId(0L);

		Path filePath = Files.createFile(Path.of(ingestionFlowFileDTO.getFilePathName()).resolve(ingestionFlowFileDTO.getFileName()));
		List<Path> mockedListPath = List.of(filePath);
		ctFlussoRiversamento.setIdentificativoFlusso("idFlow");
		PaymentsReporting paymentsReportingDTO = PaymentsReportingFaker.buildPaymentsReporting()
				.iuf("idFlow").organizationId(1L).iuv("iuv").iur("iur").transferIndex(1);
		List<PaymentsReporting> dtoList = List.of(paymentsReportingDTO);
		PaymentsReportingTransferDTO paymentsReportingTransferDTO = PaymentsReportingTransferDTO.builder()
			.orgId(1L).iuv("iuv").iur("iur").transferIndex(1).paymentOutcomeCode("0").build();

		PaymentsReportingIngestionFlowFileActivityResult expected =
			new PaymentsReportingIngestionFlowFileActivityResult("idFlow", 1L, List.of(paymentsReportingTransferDTO));

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFileDTO));
		doReturn(mockedListPath).when(ingestionFlowFileRetrieverServiceMock)
			.retrieveAndUnzipFile(ingestionFlowFileDTO.getOrganizationId(), Path.of(ingestionFlowFileDTO.getFilePathName()), ingestionFlowFileDTO.getFileName());
		when(flussoRiversamentoUnmarshallerServiceMock.unmarshal(filePath.toFile())).thenReturn(ctFlussoRiversamento);

		doNothing().when(paymentsReportingIngestionFlowFileValidatorServiceMock).validateData(ctFlussoRiversamento, ingestionFlowFileDTO);
		when(paymentsReportingMapperServiceMock.map2PaymentsReportings(ctFlussoRiversamento, ingestionFlowFileDTO)).thenReturn(dtoList);
		doReturn(1).when(paymentsReportingServiceMock).saveAll(dtoList);
		when(paymentsReportingMapperServiceMock.map(paymentsReportingDTO)).thenReturn(paymentsReportingTransferDTO);
		doNothing().when(ingestionFlowFileArchiverServiceMock)
			.archive(ingestionFlowFileDTO);

		// When
		PaymentsReportingIngestionFlowFileActivityResult result = ingestionActivity.processFile(ingestionFlowFileId);

		// Then
		assertEquals(expected, result);

		Assertions.assertFalse(filePath.toFile().exists());
	}

	@Test
	void givenNotExistentIngestionFlowFileWhenProcessFileThenFails() {
		// Given
		long ingestionFlowFileId = 123L;
		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.empty());

		// When, Then
		Assertions.assertThrows(IngestionFlowFileNotFoundException.class, () -> ingestionActivity.processFile(ingestionFlowFileId));
	}

	@Test
	void givenWrongTypeIngestionFlowFileWhenProcessFileThenFails() {
		// Given
		long ingestionFlowFileId = 123L;
		IngestionFlowFile ingestionFlowFile = IngestionFlowFileFaker.buildIngestionFlowFile()
			.ingestionFlowFileId(ingestionFlowFileId)
			.flowFileType(IngestionFlowFile.FlowFileTypeEnum.TREASURY_OPI);

		when(ingestionFlowFileServiceMock.findById(ingestionFlowFileId)).thenReturn(Optional.of(ingestionFlowFile));

		// When, Then
		Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> ingestionActivity.processFile(ingestionFlowFileId));
	}
}
