package it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.*;
import org.springframework.stereotype.Component;

@Component
public class OrgSilServiceMapper {

    public OrgSilServiceDTO map(OrgSilServiceIngestionFlowFileDTO dto, Long organizationId) {

        return OrgSilServiceDTO.builder()
                .organizationId(organizationId)
                .applicationName(dto.getApplicationName())
                .serviceUrl(dto.getServiceUrl())
                .serviceType(OrgSilServiceType.valueOf(dto.getServiceType()))
                .flagLegacy(dto.getFlagLegacy())
                .authConfig(buildAuthConfig2(dto))
                .build();
    }

    private static OrgSilServiceDTOAuthConfig buildAuthConfig2(OrgSilServiceIngestionFlowFileDTO dto) {
        boolean hasBasic = isNotBlank(dto.getLegacyBasicAuthUrl()) && isNotBlank(dto.getLegacyBasicUser()) && isNotBlank(dto.getLegacyBasicPsw());
        boolean hasJwt = isNotBlank(dto.getLegacyJwtKid()) && isNotBlank(dto.getLegacyJwtSubject()) && isNotBlank(dto.getLegacyJwtIssuer()) && isNotBlank(dto.getLegacyJwtAlgorithm()) && isNotBlank(dto.getLegacyJwtSigningKey());

        if (hasBasic && !hasJwt) {
            return SilServiceLegacyBasicAuthConfigDTO.builder()
                    .authConfig("legacyBasic")
                    .user(dto.getLegacyBasicUser())
                    .psw(dto.getLegacyBasicPsw())
                    .authUrl(dto.getLegacyBasicAuthUrl())
                    .build();
        } else if (hasJwt && !hasBasic) {
            return SilServiceLegacyJwtAuthConfigDTO.builder()
                    .authConfig("legacyJwt")
                    .kid(dto.getLegacyJwtKid())
                    .subject(dto.getLegacyJwtSubject())
                    .issuer(dto.getLegacyJwtIssuer())
                    .algorithm(JwtAlgorithm.fromValue(dto.getLegacyJwtAlgorithm()))
                    .signingKey(dto.getLegacyJwtSigningKey())
                    .build();
        } else {
            return null;
        }
    }

    private static boolean isNotBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }


}
