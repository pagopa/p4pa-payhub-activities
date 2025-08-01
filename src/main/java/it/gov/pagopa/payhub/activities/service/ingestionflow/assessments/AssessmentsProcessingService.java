package it.gov.pagopa.payhub.activities.service.ingestionflow.assessments;

import com.opencsv.exceptions.CsvException;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsDetailService;
import it.gov.pagopa.payhub.activities.connector.classification.AssessmentsService;
import it.gov.pagopa.payhub.activities.connector.debtposition.InstallmentService;
import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsErrorDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.ingestion.assessments.AssessmentsIngestionFlowFileResult;
import it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsdetail.AssessmentsDetailMapper;
import it.gov.pagopa.payhub.activities.service.files.ErrorArchiverService;
import it.gov.pagopa.payhub.activities.service.ingestionflow.IngestionFlowProcessingService;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentStatus;
import it.gov.pagopa.pu.classification.dto.generated.Assessments;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsDetailRequestBody;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.CollectionModelInstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentNoPII;
import it.gov.pagopa.pu.debtposition.dto.generated.InstallmentStatus;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptDTO;
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



    public AssessmentsProcessingService(ErrorArchiverService<AssessmentsErrorDTO> errorArchiverService, OrganizationService organizationService, AssessmentsService assessmentsService, AssessmentsDetailService assessmentsDetailService, AssessmentsDetailMapper assessmentsDetailMapper, InstallmentService installmentService, ReceiptService receiptService) {
        super(errorArchiverService, organizationService);
        this.assessmentsService = assessmentsService;
        this.assessmentsDetailService = assessmentsDetailService;
        this.assessmentsDetailMapper = assessmentsDetailMapper;
        this.installmentService = installmentService;
        this.receiptService = receiptService;
    }

    public AssessmentsIngestionFlowFileResult processAssessments(
            Iterator<AssessmentsIngestionFlowFileDTO> iterator,
            List<CsvException> readerException,
            IngestionFlowFile ingestionFlowFile, Path workingDirectory) {

        List<AssessmentsErrorDTO> errorList = new ArrayList<>();

        AssessmentsIngestionFlowFileResult ingestionFlowFileResult = new AssessmentsIngestionFlowFileResult();
        ingestionFlowFileResult.setFileVersion("1.0");
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
                String errorMessage = String.format(
                        "Organization IPA code %s does not match with the one in the ingestion flow file %s",
                        row.getOrganizationIpaCode(), ipa);
                log.error(errorMessage);
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(), "ORGANIZATION_IPA_DOES_NOT_MATCH", errorMessage);
                errorList.add(error);
                return false;

            }

            Optional <Organization> organizationOptional = organizationService.getOrganizationByIpaCode(row.getOrganizationIpaCode());
            Organization organization = null;

            if(organizationOptional.isEmpty()) {
                log.error("Organization with IPA code {} does not exist", row.getOrganizationIpaCode());
                String errorMessage = String.format(
                        "Organization with IPA code %s does not exist", row.getOrganizationIpaCode());
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(), "ORGANIZATION_IPA_DOES_NOT_EXISTS", errorMessage);
                errorList.add(error);
                return false;
            }
            else
                organization = organizationOptional.get();

            CollectionModelInstallmentNoPII collectionInstallment = installmentService.getInstallmentsByOrgIdAndIudAndStatus(organization.getOrganizationId(),
            row.getIud(),List.of(InstallmentStatus.PAID, InstallmentStatus.REPORTED));
            if(collectionInstallment.getEmbedded().getInstallmentNoPIIs().isEmpty()) {
                log.error("Debt position with IUD {} not found for organization {}", row.getIud(), organization.getOrganizationId());
                String errorMessage = String.format(
                        "Debt position with IUD %s not found for organization %s", row.getIud(), organization.getOrganizationId());
                AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                        ingestionFlowFile.getFileName(), lineNumber, row.getAssessmentCode(),
                        row.getOrganizationIpaCode(), "DEBT_POSITION_NOT_FOUND", errorMessage);
                errorList.add(error);
                return false;
            }

            InstallmentNoPII installmentNoPII = collectionInstallment.getEmbedded().getInstallmentNoPIIs().getFirst();

            ReceiptDTO receiptDTO = receiptService.getByReceiptId(installmentNoPII.getReceiptId());

            Optional <Assessments> assessmentsOptional = assessmentsService.findByOrganizationIdAndDebtPositionTypeOrgCodeAndAssessmentName(organization.getOrganizationId(),
                    row.getDebtPositionTypeOrgCode(), row.getAssessmentName());

            Assessments assessments = null;
            if (assessmentsOptional.isEmpty()) {
                AssessmentsRequestBody assessmentsRequestBody = AssessmentsRequestBody.builder()
                        .organizationId(organization.getOrganizationId())
                        .debtPositionTypeOrgCode(row.getDebtPositionTypeOrgCode())
                        .assessmentName(row.getAssessmentName())
                        .status(AssessmentStatus.CLOSED)
                        .printed(false)
                        .flagManualGeneration(true)
                        //TODO - needs to define a default user for massive
                        .operatorExternalUserId("piattaforma-unitaria")
                        .build();

                assessments = assessmentsService.createAssessment(assessmentsRequestBody);
            }else
                assessments = assessmentsOptional.get();

            AssessmentsDetailRequestBody assessmentsDetailRequestBody = assessmentsDetailMapper.map2AssessmentsDetailRequestBody(row, organization.getOrganizationId(), assessments.getAssessmentId(), receiptDTO);

            assessmentsDetailService.createAssessmentDetail(assessmentsDetailRequestBody);

            return true;

        } catch (Exception e) {
            log.error("Error processing row {} in file {}: {}", lineNumber, ingestionFlowFile.getFileName(), e.getMessage(), e);
            AssessmentsErrorDTO error = new AssessmentsErrorDTO(
                    ingestionFlowFile.getFileName(),
                    lineNumber,
                    row.getAssessmentCode(),
                    row.getOrganizationIpaCode(),
                    "PROCESS_EXCEPTION", e.getMessage());
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
