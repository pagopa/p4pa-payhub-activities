package it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrgSilServiceMapperTest {

    @Test
    void testMapJwt() {
        OrgSilServiceIngestionFlowFileDTO dto = OrgSilServiceIngestionFlowFileDTO.builder()
                .ipaCode("IPA123")
                .applicationName("APP456")
                .serviceUrl("http://example.com/service")
                .serviceType("ACTUALIZATION")
                .flagLegacy(true)
                .legacyJwtKid("KID789")
                .legacyJwtSubject("SUBJECT123")
                .legacyJwtIssuer("ISSUER456")
                .legacyJwtAlgorithm("RS256")
                .legacyJwtSigningKey("signingKey789")
                .build();

        Long orgId = 456L;

        // Act
        OrgSilServiceDTO result = new OrgSilServiceMapper().map(dto, orgId);

        SilServiceLegacyJwtAuthConfigDTO authConfig = (SilServiceLegacyJwtAuthConfigDTO) result.getAuthConfig();

        // Assert
        Assertions.assertNotNull(result.getOrganizationId());
        Assertions.assertEquals("APP456", result.getApplicationName());
        Assertions.assertEquals("http://example.com/service", result.getServiceUrl());
        Assertions.assertEquals(OrgSilServiceType.ACTUALIZATION, result.getServiceType());
        Assertions.assertTrue(result.getFlagLegacy());
        Assertions.assertEquals("KID789", authConfig.getKid());
        Assertions.assertEquals("SUBJECT123", authConfig.getSubject());
        Assertions.assertEquals("ISSUER456", authConfig.getIssuer());
        Assertions.assertEquals(JwtAlgorithm.RS256, authConfig.getAlgorithm());
        Assertions.assertEquals("signingKey789", authConfig.getSigningKey());
        TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
                "updateTraceId", "orgSilServiceId");
    }

    @Test
    void testMapBasic() {
        OrgSilServiceIngestionFlowFileDTO dto = OrgSilServiceIngestionFlowFileDTO.builder()
                .ipaCode("IPA123")
                .applicationName("APP456")
                .serviceUrl("http://example.com/service")
                .serviceType("ACTUALIZATION")
                .flagLegacy(true)
                .legacyBasicAuthUrl("http://example.com/basic-auth")
                .legacyBasicUser("User789")
                .legacyBasicPsw("Password789")
                .build();

        Long orgId = 456L;

        // Act
        OrgSilServiceDTO result = new OrgSilServiceMapper().map(dto, orgId);

        SilServiceLegacyBasicAuthConfigDTO authConfig = (SilServiceLegacyBasicAuthConfigDTO) result.getAuthConfig();

        // Assert
        Assertions.assertNotNull(result.getOrganizationId());
        Assertions.assertEquals("APP456", result.getApplicationName());
        Assertions.assertEquals("http://example.com/service", result.getServiceUrl());
        Assertions.assertEquals(OrgSilServiceType.ACTUALIZATION, result.getServiceType());
        Assertions.assertTrue(result.getFlagLegacy());
        Assertions.assertEquals("http://example.com/basic-auth", authConfig.getAuthUrl());
        Assertions.assertEquals("User789", authConfig.getUser());
        Assertions.assertEquals("Password789", authConfig.getPsw());
        TestUtils.checkNotNullFields(result, "creationDate", "updateDate", "updateOperatorExternalId",
                "updateTraceId", "orgSilServiceId");
    }

}