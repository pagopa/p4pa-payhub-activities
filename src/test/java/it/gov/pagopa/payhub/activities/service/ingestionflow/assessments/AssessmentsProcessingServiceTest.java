package it.gov.pagopa.payhub.activities.service.ingestionflow.assessments;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsDetailService;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.payhub.activities.connector.debtposition.DebtPositionTypeOrgService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail.AssessmentsDetailMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.BaseIngestionFlowProcessingServiceTest;
import it.gov.pagopa.pu.classification.dto.generated.*;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AssessmentsProcessingServiceTest extends BaseIngestionFlowProcessingServiceTest<AssessmentsIngestionFlowFileDTO, AssessmentsIngestionFlowFileResult, AssessmentsErrorDTO> {

    private static final List<InstallmentStatus> INSTALLMENT_STATUSES = List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED);

    @Mock
    private AssessmentsErrorArchiverService errorsArchiverServiceMock;
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
    private DebtPositionTypeOrgService debtPositionTypeOrgServiceMock;

    private AssessmentsProcessingService serviceSpy;

    protected AssessmentsProcessingServiceTest() {
        super(true);
    }

    @BeforeEach
    void init() {
        FileExceptionHandlerService fileExceptionHandlerService = new FileExceptionHandlerService();
        serviceSpy = Mockito.spy(new AssessmentsProcessingService(
                MAX_CONCURRENT_PROCESSING_ROWS,
                errorsArchiverServiceMock,
                organizationServiceMock,
                fileExceptionHandlerService,
                assessmentsServiceMock,
                assessmentsDetailServiceMock,
                mapperMock,
                installmentServiceMock,
                receiptServiceMock,
                debtPositionTypeOrgServiceMock
        ));
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

    @Override
    protected AssessmentsProcessingService getServiceSpy() {
        return serviceSpy;
    }

    @Override
    protected ErrorArchiverService<AssessmentsErrorDTO, AssessmentsIngestionFlowFileResult> getErrorsArchiverServiceMock() {
        return errorsArchiverServiceMock;
    }

    @Override
    protected AssessmentsIngestionFlowFileResult startProcess(Iterator<AssessmentsIngestionFlowFileDTO> rowIterator, List<CsvException> readerExceptions, IngestionFlowFile ingestionFlowFile, Path workingDirectory) {
        return serviceSpy.processAssessments(rowIterator, readerExceptions, ingestionFlowFile, workingDirectory);
    }

    @Override
    protected AssessmentsIngestionFlowFileDTO buildAndConfigureHappyUseCase(IngestionFlowFile ingestionFlowFile, int sequencingId, boolean sequenceIdAlreadySent, long rowNumber) {
        long debtPositionTypeOrgId = sequencingId * 10L;
        long assessmentId = sequencingId * 100L;
        long receiptId = sequencingId * 1000L;

        ReceiptDTO receiptDTO = new ReceiptDTO();
        receiptDTO.setReceiptId(receiptId);

        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());
        dto.setAssessmentName("ASSESSMENTNAME" + sequencingId);
        dto.setDebtPositionTypeOrgCode("DPTCODE" + sequencingId);

        CollectionModelInstallmentNoPII collectionInstallment = new CollectionModelInstallmentNoPII();
        InstallmentNoPII installment = podamFactory.manufacturePojo(InstallmentNoPII.class);
        installment.setReceiptId(receiptId);
        collectionInstallment.setEmbedded(CollectionModelInstallmentNoPIIEmbedded.builder()
                .installmentNoPIIs(List.of(installment))
                .build());
        Mockito.doReturn(collectionInstallment)
                .when(installmentServiceMock)
                .getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(), dto.getIud(), INSTALLMENT_STATUSES);

        if (!sequenceIdAlreadySent) {
            DebtPositionTypeOrg debtPositionTypeOrg = podamFactory.manufacturePojo(DebtPositionTypeOrg.class);
            debtPositionTypeOrg.setDebtPositionTypeOrgId(debtPositionTypeOrgId);
            Mockito.doReturn(debtPositionTypeOrg)
                    .when(debtPositionTypeOrgServiceMock)
                    .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getDebtPositionTypeOrgCode());

            Assessments assessments = podamFactory.manufacturePojo(Assessments.class);
            assessments.setAssessmentId(assessmentId);
            Mockito.doReturn(assessments)
                    .when(assessmentsServiceMock)
                    .createAssessment(AssessmentsRequestBody.builder()
                            .organizationId(ingestionFlowFile.getOrganizationId())
                            .debtPositionTypeOrgCode(dto.getDebtPositionTypeOrgCode())
                            .debtPositionTypeOrgId(debtPositionTypeOrgId)
                            .assessmentName(dto.getAssessmentName())
                            .status(AssessmentStatus.CLOSED)
                            .printed(false)
                            .flagManualGeneration(true)
                            .operatorExternalUserId(ingestionFlowFile.getOperatorExternalId())
                            .build()
                    );

            Mockito.doReturn(receiptDTO)
                    .when(receiptServiceMock).getByReceiptId(installment.getReceiptId());

            Mockito.doReturn(Optional.empty())
                    .when(assessmentsServiceMock)
                    .findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                            ingestionFlowFile.getOrganizationId(),
                            dto.getDebtPositionTypeOrgCode(),
                            dto.getAssessmentName());
        }

        AssessmentsDetailRequestBody assessmentsDetailRequestBody = podamFactory.manufacturePojo(AssessmentsDetailRequestBody.class);
        Mockito.doReturn(assessmentsDetailRequestBody)
                .when(mapperMock).map2AssessmentsDetailRequestBody(dto, ingestionFlowFile.getOrganizationId(), assessmentId, receiptDTO, debtPositionTypeOrgId);

        Mockito.doReturn(new AssessmentsDetail())
                .when(assessmentsDetailServiceMock).createAssessmentDetail(assessmentsDetailRequestBody);

        return dto;
    }

    @Override
    protected List<Pair<AssessmentsIngestionFlowFileDTO, List<AssessmentsErrorDTO>>> buildAndConfigureUnhappyUseCases(IngestionFlowFile ingestionFlowFile, long previousRowNumber) {
        return List.of(
                configureUnhappyUseCaseIpaMismatch(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseIudNotFound(ingestionFlowFile, ++previousRowNumber),
                configureUnhappyUseCaseDPTOrgNotFound(ingestionFlowFile, ++previousRowNumber)
        );
    }

    private Pair<AssessmentsIngestionFlowFileDTO, List<AssessmentsErrorDTO>> configureUnhappyUseCaseIpaMismatch(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode("WRONGIPACODE");

        List<AssessmentsErrorDTO> expectedErrors = List.of(
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("ORGANIZATION_IPA_MISMATCH")
                        .errorMessage("Il codice IPA WRONGIPACODE dell'ente non corrisponde a quello del file " + organization.getIpaCode())
                        .organizationIpaCode(dto.getOrganizationIpaCode())
                        .assessmentCode(dto.getAssessmentCode())
                        .build());
        return Pair.of(dto, expectedErrors);
    }

    private Pair<AssessmentsIngestionFlowFileDTO, List<AssessmentsErrorDTO>> configureUnhappyUseCaseIudNotFound(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());

        Mockito.doReturn(CollectionModelInstallmentNoPII.builder().embedded(new CollectionModelInstallmentNoPIIEmbedded()).build())
                .when(installmentServiceMock)
                .getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(),
                        dto.getIud(), INSTALLMENT_STATUSES);

        List<AssessmentsErrorDTO> expectedErrors = List.of(
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("DEBT_POSITION_BY_IUD_NOT_FOUND")
                        .errorMessage("La posizione debitoria con IUD %s non e' stata trovata per l'ente".formatted(dto.getIud()))
                        .organizationIpaCode(organization.getIpaCode())
                        .assessmentCode(dto.getAssessmentCode())
                        .build());

        return Pair.of(dto, expectedErrors);
    }

    private Pair<AssessmentsIngestionFlowFileDTO, List<AssessmentsErrorDTO>> configureUnhappyUseCaseDPTOrgNotFound(IngestionFlowFile ingestionFlowFile, long rowNumber) {
        AssessmentsIngestionFlowFileDTO dto = podamFactory.manufacturePojo(AssessmentsIngestionFlowFileDTO.class);
        dto.setOrganizationIpaCode(organization.getIpaCode());

        InstallmentNoPII installment = podamFactory.manufacturePojo(InstallmentNoPII.class);
        CollectionModelInstallmentNoPII collectionInstallment = new CollectionModelInstallmentNoPII();
        collectionInstallment.setEmbedded(CollectionModelInstallmentNoPIIEmbedded.builder()
                .installmentNoPIIs(List.of(installment))
                .build());
        Mockito.doReturn(collectionInstallment)
                .when(installmentServiceMock)
                .getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(), dto.getIud(), INSTALLMENT_STATUSES);

        var receiptDTOMock = mock(it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO.class);
        Mockito.doReturn(receiptDTOMock)
                .when(receiptServiceMock)
                .getByReceiptId(installment.getReceiptId());

        Mockito.doReturn(Optional.empty())
                .when(assessmentsServiceMock)
                .findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(
                        ingestionFlowFile.getOrganizationId(),
                        dto.getDebtPositionTypeOrgCode(),
                        dto.getAssessmentName()
                );

        Mockito.doReturn(null)
                .when(debtPositionTypeOrgServiceMock)
                .getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), dto.getDebtPositionTypeOrgCode());

        List<AssessmentsErrorDTO> expectedErrors = List.of(
                AssessmentsErrorDTO.builder()
                        .fileName(ingestionFlowFile.getFileName())
                        .rowNumber(rowNumber)
                        .errorCode("DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND")
                        .errorMessage("Il tipo posizione debitoria impostato con codice %s non e' stato trovato per l'ente".formatted(dto.getDebtPositionTypeOrgCode()))
                        .organizationIpaCode(organization.getIpaCode())
                        .assessmentCode(dto.getAssessmentCode())
                        .build());

        return Pair.of(dto, expectedErrors);
    }
}
