package it.gov.pagopa.payhub.activities.mapper.ingestionflow.assessmentsregistry;

import it.gov.pagopa.payhub.activities.dto.ingestion.assessmentsregistry.AssessmentsRegistryIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistryStatus;
import org.springframework.stereotype.Service;

@Service
public class AssessmentsRegistryMapper {

    public AssessmentsRegistry map(AssessmentsRegistryIngestionFlowFileDTO dto, Long organizationId) {

        return AssessmentsRegistry.builder()

                .assessmentCode(dto.getAssessmentCode())
                .assessmentDescription(dto.getAssessmentDescription())

                .debtPositionTypeOrgCode(dto.getDebtPositionTypeOrgCode())

                .officeCode(dto.getOfficeCode())
                .officeDescription(dto.getOfficeDescription())

                .operatingYear(dto.getOperatingYear())
                .organizationId(organizationId)

                .sectionCode(dto.getSectionCode())
                .sectionDescription(dto.getSectionDescription())

                .status(AssessmentsRegistryStatus.fromValue(dto.getStatus()))

                .build();
    }
}

