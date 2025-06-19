package it.gov.pagopa.payhub.activities.util.faker;

import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistry;
import it.gov.pagopa.pu.classification.dto.generated.AssessmentsRegistryStatus;

public class AssessmentsRegistryFaker {

    private AssessmentsRegistryFaker() {
    }

    public static AssessmentsRegistry buildAssessmentsRegistry() {
        return TestUtils.getPodamFactory().manufacturePojo(AssessmentsRegistry.class)
                .assessmentCode("assessmentCode")
                .assessmentDescription("des")
                .debtPositionTypeOrgCode("debtPositionTypeOrgCode")
                .operatingYear("2025")
                .officeCode("officeCode")
                .officeDescription("officeDescription")
                .organizationId(123L)
                .sectionCode("sectionCode")
                .sectionDescription("sectionDescription")
                .status(AssessmentsRegistryStatus.ACTIVE)
                ;
    }
}
