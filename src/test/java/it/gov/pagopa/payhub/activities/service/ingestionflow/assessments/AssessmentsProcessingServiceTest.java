package it.gov.pagopa.payhub.activities.service.ingestionflow.assessments;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsDetailService;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail.AssessmentsDetailMapper;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AssessmentsProcessingServiceTest {

    private static final List<InstallmentStatus> INSTALLMENT_STATUSES = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

    @Mock
    private AssessmentsErrorArchiverServcie errorsArchiverServiceMock;
    @Mock
    private Path workingDirectory;
    @Mock
    private AssessmentsDetailMapper mapperMock;
    @Mock
    private AssessmentsService assessmentsServiceMock;
    @Mock
    private AssessmentsDetailService assessmentsDetailServiceMock;
    @Mock
    private InstallmentService installmentServiceMock;
    @Mock
    private ReceiptService receiptServiceMock;
    @Mock
    private OrganizationService organizationServiceMock;
    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    private AssessmentsProcessingService service;

    private static final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        service = new AssessmentsProcessingService(1,
                errorsArchiverServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService,
                assessmentsServiceMock,
                assessmentsDetailServiceMock,
                mapperMock,
                installmentServiceMock,
                receiptServiceMock,
                debtPositionTypeOrgServiceMock
        );
    }

    @AfterEach
    void verifyNoMoreInteractions() {
        Mockito.verifyNoMoreInteractions(
                errorsArchiverServiceMock,
                organizationServiceMock,
                assessmentsServiceMock,
                assessmentsDetailServiceMock,
                mapperMock,
                installmentServiceMock,
                receiptServiceMock,
                debtPositionTypeOrgServiceMock
        );
    }

    @Test
    void whenGetSequencingIdThenReturnExpectedValue() {
        // Given
        AssessmentsIngestionFlowFileDTO row = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);

        // When
        String result = service.getSequencingId(row);

        // Then
        assertEquals(
                row.getDebtPositionTypeOrgCode() + "_" + row.getAssessmentName(),
                result);
    }

    @Test
    void processAssessmentsWithIpaErrors() {
        // Given
        String ipaCode = "IPA123";
        String ipaWrong = "IPA123_WRONG";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaWrong);
        organization.setOrganizationId(ingestionFlowFile.getOrganizationId());

        Mockito.when(organizationServiceMock.getOrganizationById(ingestionFlowFile.getOrganizationId()))
                        .thenReturn(Optional.of(organization));

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsIngestionFlowFileResult result = service.processAssessments(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(1L)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA IPA123 dell'ente non corrisponde a quello del file IPA123_WRONG")
                        .organizationIpaCode(ipaCode)
                        .assessmentCode(dto.getAssessmentCode())
                        .build()));
    }

    @Test
    void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
        // Given
        long lineNumber = 1L;
        String orgIpaCode = "IPA123";
        AssessmentsIngestionFlowFileDTO row = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        row.setOrganizationIpaCode(orgIpaCode);
        row.setDebtPositionTypeOrgCode("DPT001");
        row.setAssessmentName("ASSESSMENT1");
        row.setIud("IUD1");

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode(orgIpaCode);

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOperatorExternalId("OPERATOR");
        ingestionFlowFile.setOrganizationId(123L);

        CollectionModelInstallmentNoPII collectionInstallment = new CollectionModelInstallmentNoPII();
        collectionInstallment.setEmbedded(CollectionModelInstallmentNoPIIEmbedded.builder()
                .installmentNoPIIs(List.of(mock(InstallmentNoPII.class)))
                .build());
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(), row.getIud(), INSTALLMENT_STATUSES))
                .thenReturn(collectionInstallment);

        AssessmentsDetailRequestBody assessmentsDetailRequestBody = mock(AssessmentsDetailRequestBody.class);
        Assessments assessments = mock(Assessments.class);
        Mockito.when(assessments.getAssessmentId()).thenReturn(456L);
        Mockito.when(assessmentsServiceMock.createAssessment(any())).thenReturn(assessments);

        DebtPositionTypeOrg debtPositionTypeOrg = mock(DebtPositionTypeOrg.class);

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), row.getDebtPositionTypeOrgCode()))
                .thenReturn(debtPositionTypeOrg);


        var receiptDTOMock = mock(it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO.class);
        Mockito.when(receiptServiceMock.getByReceiptId(any())).thenReturn(receiptDTOMock);
        Mockito.when(mapperMock.map2AssessmentsDetailRequestBody(row, 123L, 456L, receiptDTOMock, debtPositionTypeOrg.getDebtPositionTypeOrgId())).thenReturn(assessmentsDetailRequestBody);

        Mockito.when(assessmentsServiceMock.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                ingestionFlowFile.getOrganizationId(),
                row.getDebtPositionTypeOrgCode(),
                row.getAssessmentName()
        )).thenReturn(Optional.empty());

        // When
        List<AssessmentsErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(assessmentsServiceMock).createAssessment(Mockito.any(AssessmentsRequestBody.class));
        Mockito.verify(assessmentsDetailServiceMock).createAssessmentDetail(assessmentsDetailRequestBody);
    }

    @Test
    void processAssessmentsWithErrors() {
        // Given
        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaCode);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(Optional.of(organization));

        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(),
                        dto.getIud(), INSTALLMENT_STATUSES))
                .thenReturn(CollectionModelInstallmentNoPII.builder().embedded(new CollectionModelInstallmentNoPIIEmbedded()).build());

        Mockito.when(errorsArchiverServiceMock.archiveErrorFiles(workingDirectory, ingestionFlowFile))
                .thenReturn("zipFileName.csv");

        // When
        AssessmentsIngestionFlowFileResult result = service.processAssessments(
                Stream.of(dto).iterator(), List.of(new CsvException("DUMMYERROR")),
                ingestionFlowFile, workingDirectory);

        // Then
        assertEquals(2, result.getTotalRows());
        assertEquals(0, result.getProcessedRows());
        assertEquals("Some rows have failed", result.getErrorDescription());
        assertEquals("zipFileName.csv", result.getDiscardedFileName());

        Mockito.verify(errorsArchiverServiceMock).writeErrors(workingDirectory, ingestionFlowFile, List.of(
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(-1L)
                        .errorCode("CSV_GENERIC_ERROR")
                        .errorMessage("Errore generico nella lettura del file: DUMMYERROR")
                        .build(),
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(2L)
                        .errorCode("DEBT_POSITION_BY_IUD_NOT_FOUND")
                        .errorMessage("La posizione debitoria con IUD null non e' stata trovata per l'ente")
                        .organizationIpaCode(ipaCode)
                        .build()));
    }

    @Test
    void consumeRowWithNoInstallmentsShouldReturnError() {
        // Given
        long lineNumber = 2L;
        String orgIpaCode = "IPA123";
        AssessmentsIngestionFlowFileDTO row = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        row.setOrganizationIpaCode(orgIpaCode);
        row.setIud("IUD1");

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode(orgIpaCode);
        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);

        CollectionModelInstallmentNoPII collectionInstallment = new CollectionModelInstallmentNoPII();
        collectionInstallment.setEmbedded(CollectionModelInstallmentNoPIIEmbedded.builder()
                .installmentNoPIIs(Collections.emptyList())
                .build());
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(), row.getIud(), INSTALLMENT_STATUSES))
                .thenReturn(collectionInstallment);

        // When
        List<AssessmentsErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(FileErrorCode.DEBT_POSITION_BY_IUD_NOT_FOUND.name(), result.getFirst().getErrorCode());
        Assertions.assertTrue(result.getFirst().getErrorMessage().contains("La posizione debitoria con IUD IUD1 non e' stata trovata per l'ente"));
    }

    @Test
    void consumeRowWithNoDPTypeOrgShouldReturnError() {
        long lineNumber = 1L;
        String orgIpaCode = "IPA123";
        AssessmentsIngestionFlowFileDTO row = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        row.setOrganizationIpaCode(orgIpaCode);
        row.setDebtPositionTypeOrgCode("DPT001");
        row.setAssessmentName("ASSESSMENT1");
        row.setIud("IUD1");

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode(orgIpaCode);

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);

        CollectionModelInstallmentNoPII collectionInstallment = new CollectionModelInstallmentNoPII();
        collectionInstallment.setEmbedded(CollectionModelInstallmentNoPIIEmbedded.builder()
                .installmentNoPIIs(List.of(mock(InstallmentNoPII.class)))
                .build());
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(), row.getIud(), INSTALLMENT_STATUSES)).thenReturn(collectionInstallment);

        var receiptDTOMock = mock(it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO.class);
        Mockito.when(receiptServiceMock.getByReceiptId(any())).thenReturn(receiptDTOMock);

        Mockito.when(assessmentsServiceMock.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                ingestionFlowFile.getOrganizationId(),
                row.getDebtPositionTypeOrgCode(),
                row.getAssessmentName()
        )).thenReturn(Optional.empty());

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), row.getDebtPositionTypeOrgCode()))
                .thenReturn(null);

        // When
        List<AssessmentsErrorDTO> result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, ingestionFlowFile);

        // Then
        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(FileErrorCode.DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND.name(), result.getFirst().getErrorCode());
        Assertions.assertTrue(result.getFirst().getErrorMessage().contains("Il tipo posizione debitoria impostato con codice DPT001 non e' stato trovato per l'ente"));
    }
}
