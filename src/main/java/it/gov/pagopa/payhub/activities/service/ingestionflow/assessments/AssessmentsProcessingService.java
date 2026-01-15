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
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.files.FileExceptionHandlerService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentStatus;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.*;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@Lazy
@Slf4j
public class AssessmentsProcessingService extends
        IngestionFlowProcessingService<AssessmentsIngestionFlowFileDTO, AssessmentsIngestionFlowFileResult, AssessmentsErrorDTO> {

    private final AssessmentsService assessmentsService;
    private final AssessmentsDetailService assessmentsDetailService;
    private final AssessmentsDetailMapper assessmentsDetailMapper;
    private final InstallmentService installmentService;
    private final ReceiptService receiptService;
    private final DebtPositionTypeOrgService debtPositionTypeOrgService;
    private final FileExceptionHandlerService fileExceptionHandlerService;

    public AssessmentsProcessingService(ErrorArchiverService<AssessmentsErrorDTO> errorArchiverService, OrganizationService organizationService, FileExceptionHandlerService fileExceptionHandlerService, AssessmentsService assessmentsService, AssessmentsDetailService assessmentsDetailService, AssessmentsDetailMapper assessmentsDetailMapper, InstallmentService installmentService, ReceiptService receiptService, DebtPositionTypeOrgService debtPositionTypeOrgService) {
        super(errorArchiverService, organizationService, fileExceptionHandlerService);
        this.assessmentsService = assessmentsService;
        this.assessmentsDetailService = assessmentsDetailService;
        this.assessmentsDetailMapper = assessmentsDetailMapper;
        this.installmentService = installmentService;
        this.receiptService = receiptService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
        this.fileExceptionHandlerService = fileExceptionHandlerService;
    }

    public AssessmentsIngestionFlowFileResult processAssessments(
            Iterator<AssessmentsIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

        List<AssessmentsErrorDTO> errorList = new ArrayList<>();

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setFileVersion(ingestionFlowFile.getFileVersion());
        ingestionFlowFileResult.setOrganizationId(ingestionFlowFile.getOrganizationId());

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }


    @Override
    protected boolean consumeRow(long lineNumber,
                                 AssessmentsIngestionFlowFileDTO row,
                                 AssessmentsIngestionFlowFileResult ingestionFlowFileResult,
                                 List<AssessmentsErrorDTO> errorList, IngestionFlowFile ingestionFlowFile) {
        try {
            String ipa = ingestionFlowFileResult.getIpaCode();
            if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
                log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getOrganizationIpaCode(), ipa);
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
                        FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getOrganizationIpaCode(), ipa));
                errorList.add(error);
                return false;
            }

            Optional<Organization> organizationOptional = organizationService.getOrganizationByIpaCode(row.getOrganizationIpaCode());
            Organization organization = null;

            if (organizationOptional.isEmpty()) {
                log.error("Organization with IPA code {} does not exist", row.getOrganizationIpaCode());
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.ORGANIZATION_IPA_DOES_NOT_EXISTS.name(),
                        FileErrorCode.ORGANIZATION_IPA_DOES_NOT_EXISTS.format(row.getOrganizationIpaCode()));
                errorList.add(error);
                return false;
            } else
                organization = organizationOptional.get();

            CollectionModelInstallmentNoPII collectionInstallment = installmentService.getInstallmentsByOrgIdAndIudAndStatus(organization.getOrganizationId(),
                    row.getIud(), List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED));
            if (collectionInstallment.getEmbedded().getInstallmentNoPIIs().isEmpty()) {
                log.error("Debt position with IUD {} not found for organization {}", row.getIud(), organization.getOrganizationId());
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.DEBT_POSITION_BY_IUD_NOT_FOUND.name(),
                        FileErrorCode.DEBT_POSITION_BY_IUD_NOT_FOUND.format(row.getIud()));
                errorList.add(error);
                return false;
            }

            InstallmentNoPII installmentNoPII = collectionInstallment.getEmbedded().getInstallmentNoPIIs().getFirst();

            ReceiptDTO receiptDTO = receiptService.getByReceiptId(installmentNoPII.getReceiptId());

            Optional<Assessments> assessmentsOptional = assessmentsService.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(organization.getOrganizationId(),
                    row.getDebtPositionTypeOrgCode(), row.getAssessmentName());

            DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(organization.getOrganizationId(), row.getDebtPositionTypeOrgCode());
            if (debtPositionTypeOrg == null) {
                log.error("Debt position type org not found for org {} and code {}", organization.getOrganizationId(), row.getDebtPositionTypeOrgCode());
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(),
                        FileErrorCode.DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND.name(),
                        FileErrorCode.DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND.format(row.getDebtPositionTypeOrgCode()));
                errorList.add(error);
                return false;
            }

            Assessments assessments = null;
            if (assessmentsOptional.isEmpty()) {
                AssessmentsRequestBody assessmentsRequestBody = AssessmentsRequestBody.builder()
                        .organizationId(organization.getOrganizationId())
                        .debtPositionTypeOrgCode(row.getDebtPositionTypeOrgCode())
                        .debtPositionTypeOrgId(debtPositionTypeOrg.getDebtPositionTypeOrgId())
                        .assessmentName(row.getAssessmentName())
                        .status(AssessmentStatus.CLOSED)
                        .printed(false)
                        .flagManualGeneration(true)
                        //TODO - needs to define a default user for massive
                        .operatorExternalUserId("piattaforma-unitaria")
                        .build();

                assessments = assessmentsService.createAssessment(assessmentsRequestBody);
            } else
                assessments = assessmentsOptional.get();

            AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailMapper.map2AssessmentsDetailRequestBody(row, organization.getOrganizationId(), assessments.getAssessmentId(), receiptDTO, debtPositionTypeOrg.getDebtPositionTypeOrgId());

            assessmentsDetailService.createAssessmentDetail(assessmentsDetailRequestBody);

            return true;

        } catch (Exception e) {
            log.error("Error processing row {} in file {}: {}", lineNumber, ingestionFlowFile.getFileName(), e.getMessage(), e);
            FileExceptionHandlerService.ErrorDetails errorDetails = fileExceptionHandlerService.mapExceptionToErrorCodeAndMessage(e.getMessage());
            AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                    ingestionFlowFile.getFileName(),
                    lineNumber,
                    row.getAssessmentCode(),
                    row.getOrganizationIpaCode(),
                    errorDetails.getErrorCode(), errorDetails.getErrorMessage());
            errorList.add(error);
            log.info("Current error list size after handleProcessingError: {}", errorList.size());
            return false;
        }
    }

    @Override
    protected AssessmentsErrorDTO buildErrorDto(String fileName, long lineNumber, String errorCode, String message) {
        return AssessmentsErrorDTO.builder()
                .fileName(fileName)
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }
}
