package it.gov.pagopa.payhub.activities.dto.assessments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentsRegistrySemanticKey {
    private Long organizationId;
    private String debtPositionTypeOrgCode;
    private String sectionCode;
    private String officeCode;
    private String assessmentCode;
    private String operatingYear;
}
