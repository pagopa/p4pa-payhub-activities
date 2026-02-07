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
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.*;

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

    public AssessmentsProcessingService(
            @Value("${ingestion-flow-files.assessments.max-concurrent-processing-rows}") int maxConcurrentProcessingRows,

            ErrorArchiverService<AssessmentsErrorDTO> errorArchiverService,
            OrganizationService organizationService,
            FileExceptionHandlerService fileExceptionHandlerService,
            AssessmentsService assessmentsService,
            AssessmentsDetailService assessmentsDetailService,
            AssessmentsDetailMapper assessmentsDetailMapper,
            InstallmentService installmentService,
            ReceiptService receiptService,
            DebtPositionTypeOrgService debtPositionTypeOrgService) {
        super(maxConcurrentProcessingRows, errorArchiverService, organizationService, fileExceptionHandlerService);
        this.assessmentsService = assessmentsService;
        this.assessmentsDetailService = assessmentsDetailService;
        this.assessmentsDetailMapper = assessmentsDetailMapper;
        this.installmentService = installmentService;
        this.receiptService = receiptService;
        this.debtPositionTypeOrgService = debtPositionTypeOrgService;
    }

    public AssessmentsIngestionFlowFileResult processAssessments(
            Iterator<AssessmentsIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

        List<AssessmentsErrorDTO> errorList = new ArrayList<>();

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();

        String ipaCode = getIpaCodeByOrganizationId(ingestionFlowFile.getOrganizationId());
        ingestionFlowFileResult.setIpaCode(ipaCode);

        process(iterator, readerException, ingestionFlowFileResult, ingestionFlowFile, errorList, workingDirectory);
        return ingestionFlowFileResult;
    }

    @Override
    protected String getSequencingId(AssessmentsIngestionFlowFileDTO row) {
        return row.getDebtPositionTypeOrgCode() + "_" + row.getAssessmentName();
    }

    @Override
    protected List<AssessmentsErrorDTO> consumeRow(long lineNumber,
                                                   AssessmentsIngestionFlowFileDTO row,
                                                   AssessmentsIngestionFlowFileResult ingestionFlowFileResult,
                                                   IngestionFlowFile ingestionFlowFile) {
        String ipa = ingestionFlowFileResult.getIpaCode();
        if (!row.getOrganizationIpaCode().equalsIgnoreCase(ipa)) {
            log.error("Organization IPA code {} does not match with the one in the ingestion flow file {}", row.getOrganizationIpaCode(), ipa);
            AssessmentsErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, row,
                    FileErrorCode.ORGANIZATION_IPA_MISMATCH.name(),
                    FileErrorCode.ORGANIZATION_IPA_MISMATCH.format(row.getOrganizationIpaCode(), ipa));
            return List.of(error);
        }

        CollectionModelInstallmentNoPII collectionInstallment = installmentService.getInstallmentsByOrgIdAndIudAndStatus(ingestionFlowFile.getOrganizationId(),
                row.getIud(), List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED));
        if (CollectionUtils.isEmpty(Objects.requireNonNull(collectionInstallment.getEmbedded()).getInstallmentNoPIIs())) {
            log.error("Debt position with IUD {} not found for organization {}", row.getIud(), ingestionFlowFile.getOrganizationId());
            AssessmentsErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, row,
                    FileErrorCode.DEBT_POSITION_BY_IUD_NOT_FOUND.name(),
                    FileErrorCode.DEBT_POSITION_BY_IUD_NOT_FOUND.format(row.getIud()));
            return List.of(error);
        }

        InstallmentNoPII installmentNoPII = collectionInstallment.getEmbedded().getInstallmentNoPIIs().getFirst();

        ReceiptDTO receiptDTO = receiptService.getByReceiptId(installmentNoPII.getReceiptId());

        Optional<Assessments> assessmentsOptional = assessmentsService.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(ingestionFlowFile.getOrganizationId(),
                row.getDebtPositionTypeOrgCode(), row.getAssessmentName());

        DebtPositionTypeOrg debtPositionTypeOrg = debtPositionTypeOrgService.getDebtPositionTypeOrgByOrganizationIdAndCode(ingestionFlowFile.getOrganizationId(), row.getDebtPositionTypeOrgCode());
        if (debtPositionTypeOrg == null) {
            log.error("Debt position type org not found for org {} and code {}", ingestionFlowFile.getOrganizationId(), row.getDebtPositionTypeOrgCode());
            AssessmentsErrorDTO error = buildErrorDto(
                    ingestionFlowFile, lineNumber, row,
                    FileErrorCode.DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND.name(),
                    FileErrorCode.DEBT_POSITION_TYPE_ORG_BY_CODE_NOT_FOUND.format(row.getDebtPositionTypeOrgCode()));
            return List.of(error);
        }

        Assessments assessments;
        if (assessmentsOptional.isEmpty()) {
            AssessmentsRequestBody assessmentsRequestBody = AssessmentsRequestBody.builder()
                    .organizationId(Objects.requireNonNull(ingestionFlowFile.getOrganizationId()))
                    .debtPositionTypeOrgCode(row.getDebtPositionTypeOrgCode())
                    .debtPositionTypeOrgId(Objects.requireNonNull(debtPositionTypeOrg.getDebtPositionTypeOrgId()))
                    .assessmentName(row.getAssessmentName())
                    .status(AssessmentStatus.CLOSED)
                    .printed(false)
                    .flagManualGeneration(true)
                    .operatorExternalUserId(ingestionFlowFile.getOperatorExternalId())
                    .build();

            assessments = assessmentsService.createAssessment(assessmentsRequestBody);
        } else
            assessments = assessmentsOptional.get();

        AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailMapper.map2AssessmentsDetailRequestBody(row, ingestionFlowFile.getOrganizationId(), assessments.getAssessmentId(), receiptDTO, debtPositionTypeOrg.getDebtPositionTypeOrgId());

        assessmentsDetailService.createAssessmentDetail(assessmentsDetailRequestBody);

        return Collections.emptyList();
    }

    @Override
    protected AssessmentsErrorDTO buildErrorDto(IngestionFlowFile ingestionFlowFile, long lineNumber, AssessmentsIngestionFlowFileDTO row, String errorCode, String message) {
        AssessmentsErrorDTO errorDTO = AssessmentsErrorDTO.builder()
                .fileName(ingestionFlowFile.getFileName())
                .rowNumber(lineNumber)
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
        if (row != null) {
            errorDTO.setAssessmentCode(row.getAssessmentCode());
            errorDTO.setOrganizationIpaCode(row.getOrganizationIpaCode());
        }
        return errorDTO;
    }
}
