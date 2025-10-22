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
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail.AssessmentsDetailMapper;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPIIEmbedded;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static it.gov.pagopa.payhub.activities.util.faker.IngestionFlowFileFaker.buildIngestionFlowFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AssessmentsProcessingServiceTest {

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
    private AssessmentsProcessingService service;

    @Mock
    private OrganizationService organizationServiceMock;

    @Mock
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    @BeforeEach
    void setUp() {
        service = new AssessmentsProcessingService(
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
    void processAssessmentsWithIpaErrors() {
        // Given
        String ipaCode = "IPA123";
        String ipaWrong = "IPA123_WRONG";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsIngestionFlowFileDTO dto = mock(AssessmentsIngestionFlowFileDTO.class);
        Mockito.when(dto.getOrganizationIpaCode()).thenReturn(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaWrong);
        Optional<Organization> organizationOptional = Optional.of(organization);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(organizationOptional);

        // When
        AssessmentsIngestionFlowFileResult result = service.processAssessments(
                Stream.of(dto).iterator(), List.of(),
                ingestionFlowFile, workingDirectory);

        // Then
        Assertions.assertSame(ingestionFlowFile.getOrganizationId(), result.getOrganizationId());
        Assertions.assertEquals(0L, result.getProcessedRows());
        Assertions.assertEquals(1L, result.getTotalRows());
    }

    @Test
    void consumeRowWithMatchingIpaCodeProcessesSuccessfully() {
        // Given
        long lineNumber = 1L;
        AssessmentsIngestionFlowFileDTO row = mock(AssessmentsIngestionFlowFileDTO.class);

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode("IPA123");

        IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

        Organization organization = new Organization();
        organization.setIpaCode("IPA123");
        organization.setOrganizationId(123L);

        var collectionInstallment = mock(CollectionModelInstallmentNoPII.class);
        var embedded = mock(CollectionModelInstallmentNoPIIEmbedded.class);
        Mockito.when(collectionInstallment.getEmbedded()).thenReturn(embedded);
        Mockito.when(embedded.getInstallmentNoPIIs()).thenReturn(List.of(mock(InstallmentNoPII.class)));
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(Mockito.eq(123L), any(), any())).thenReturn(collectionInstallment);

        AssessmentsDetailRequestBody assessmentsDetailRequestBody = mock(AssessmentsDetailRequestBody.class);
        Assessments assessments = mock(Assessments.class);
        Mockito.when(assessments.getAssessmentId()).thenReturn(456L);
        Mockito.when(assessmentsServiceMock.createAssessment(any())).thenReturn(assessments);
        List<AssessmentsErrorDTO> errorList = new ArrayList<>();

        Mockito.when(row.getOrganizationIpaCode()).thenReturn("IPA123");
        Mockito.when(row.getDebtPositionTypeOrgCode()).thenReturn("DPT001");
        Mockito.when(row.getAssessmentName()).thenReturn("ASSESSMENT1");
        Mockito.when(row.getIud()).thenReturn("IUD1");

        DebtPositionTypeOrg debtPositionTypeOrg = mock(DebtPositionTypeOrg.class);

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(organization.getOrganizationId(), row.getDebtPositionTypeOrgCode()))
                .thenReturn(debtPositionTypeOrg);

        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

        var receiptDTOMock = mock(it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO.class);
        Mockito.when(receiptServiceMock.getByReceiptId(any())).thenReturn(receiptDTOMock);
        Mockito.when(mapperMock.map2AssessmentsDetailRequestBody(row, 123L,456L, receiptDTOMock, debtPositionTypeOrg.getDebtPositionTypeOrgId())).thenReturn(assessmentsDetailRequestBody);

        // When
        boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

        // Then
        Assertions.assertTrue(result);
        Mockito.verify(assessmentsServiceMock).createAssessment(Mockito.any(AssessmentsRequestBody.class));
        Mockito.verify(assessmentsDetailServiceMock).createAssessmentDetail(assessmentsDetailRequestBody);
        Assertions.assertTrue(errorList.isEmpty());
    }

    @Test
    void processAssessmentsWithErrors() {

        // Given
        String ipaCode = "IPA123";

        IngestionFlowFile ingestionFlowFile = buildIngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        AssessmentsIngestionFlowFileDTO dto = mock(AssessmentsIngestionFlowFileDTO.class);
        Mockito.when(dto.getOrganizationIpaCode()).thenReturn(ipaCode);

        Organization organization = new Organization();
        organization.setIpaCode(ipaCode);
        Optional<Organization> organizationOptional = Optional.of(organization);

        Mockito.when(organizationServiceMock.getOrganizationById(any())).thenReturn(organizationOptional);

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

        Mockito.verifyNoInteractions(assessmentsDetailServiceMock);
        Mockito.verify(errorsArchiverServiceMock).archiveErrorFiles(workingDirectory, ingestionFlowFile);
    }

    @Test
    void consumeRowWithNoInstallmentsShouldReturnFalseAndAddError() {
        // Given
        long lineNumber = 2L;
        AssessmentsIngestionFlowFileDTO row = mock(AssessmentsIngestionFlowFileDTO.class);
        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode("IPA123");
        IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

        Organization organization = new Organization();
        organization.setIpaCode("IPA123");
        organization.setOrganizationId(123L);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

        var collectionInstallment = mock(CollectionModelInstallmentNoPII.class);
        var embedded = mock(CollectionModelInstallmentNoPIIEmbedded.class);
        Mockito.when(collectionInstallment.getEmbedded()).thenReturn(embedded);
        Mockito.when(embedded.getInstallmentNoPIIs()).thenReturn(new ArrayList<>());
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(Mockito.eq(123L), any(), any())).thenReturn(collectionInstallment);

        List<AssessmentsErrorDTO> errorList = new ArrayList<>();
        Mockito.when(row.getOrganizationIpaCode()).thenReturn("IPA123");
        Mockito.when(row.getIud()).thenReturn("IUD1");

        // When
        boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

        // Then
        Assertions.assertFalse(result);
        Assertions.assertEquals(1, errorList.size());
        Assertions.assertEquals("DEBT_POSITION_NOT_FOUND", errorList.getFirst().getErrorCode());
        Assertions.assertTrue(errorList.getFirst().getErrorMessage().contains("Debt position with IUD IUD1 not found"));
    }

    @Test
    void consumeRowWithNoDPTypeOrgShouldReturnFalseAndAddError() {
        long lineNumber = 1L;
        AssessmentsIngestionFlowFileDTO row = mock(AssessmentsIngestionFlowFileDTO.class);

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setIpaCode("IPA123");

        IngestionFlowFile ingestionFlowFile = mock(IngestionFlowFile.class);

        Organization organization = new Organization();
        organization.setIpaCode("IPA123");
        organization.setOrganizationId(123L);
        Mockito.when(organizationServiceMock.getOrganizationByIpaCode("IPA123")).thenReturn(Optional.of(organization));

        var collectionInstallment = mock(CollectionModelInstallmentNoPII.class);
        var embedded = mock(CollectionModelInstallmentNoPIIEmbedded.class);
        Mockito.when(collectionInstallment.getEmbedded()).thenReturn(embedded);
        Mockito.when(embedded.getInstallmentNoPIIs()).thenReturn(List.of(mock(InstallmentNoPII.class)));
        Mockito.when(installmentServiceMock.getInstallmentsByOrgIdAndIudAndStatus(Mockito.eq(123L), any(), any())).thenReturn(collectionInstallment);

        List<AssessmentsErrorDTO> errorList = new ArrayList<>();

        Mockito.when(row.getOrganizationIpaCode()).thenReturn("IPA123");
        Mockito.when(row.getDebtPositionTypeOrgCode()).thenReturn("DPT001");
        Mockito.when(row.getAssessmentName()).thenReturn("ASSESSMENT1");
        Mockito.when(row.getIud()).thenReturn("IUD1");

        var receiptDTOMock = mock(it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO.class);
        Mockito.when(receiptServiceMock.getByReceiptId(any())).thenReturn(receiptDTOMock);

        Mockito.when(debtPositionTypeOrgServiceMock.getDebtPositionTypeOrgByOrganizationIdAndCode(organization.getOrganizationId(), row.getDebtPositionTypeOrgCode()))
                .thenReturn(null);

        // When
        boolean result = service.consumeRow(lineNumber, row, ingestionFlowFileResult, errorList, ingestionFlowFile);

        // Then
        Assertions.assertFalse(result);
        Assertions.assertEquals(1, errorList.size());
        Assertions.assertEquals("DEBT_POSITION_TYPE_ORG_NOT_FOUND", errorList.getFirst().getErrorCode());
        Assertions.assertTrue(errorList.getFirst().getErrorMessage().contains("Debt position type org not found for org 123 and code DPT001"));
    }
}
