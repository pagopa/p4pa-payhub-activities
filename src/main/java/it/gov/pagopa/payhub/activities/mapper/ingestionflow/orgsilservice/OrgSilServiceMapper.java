package it.gov.pagopa.payhub.activities.mapper.ingestionflow.orgsilservice;

import it.gov.pagopa.payhub.activities.dto.ingestion.orgsilservice.OrgSilServiceIngestionFlowFileDTO;
import it.gov.pagopa.pu.organization.dto.generated.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class OrgSilServiceMapper {

    public OrgSilServiceDTO map(OrgSilServiceIngestionFlowFileDTO dto, Long organizationId) {
        if (!StringUtils.hasText(dto.getServiceType())) {
            throw new IllegalArgumentException("serviceType is null or empty");
        }
        OrgSilServiceType serviceType;
        try {
            serviceType = OrgSilServiceType.valueOf(dto.getServiceType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("serviceType not valid: " + dto.getServiceType(), e);
        }
        return OrgSilServiceDTO.builder()
                .organizationId(organizationId)
                .applicationName(dto.getApplicationName())
                .serviceUrl(dto.getServiceUrl())
                .serviceType(serviceType)
                .flagLegacy(dto.getFlagLegacy())
                .authConfig(buildAuthConfig2(dto))
                .build();
    }

    private static OrgSilServiceDTOAuthConfig buildAuthConfig2(OrgSilServiceIngestionFlowFileDTO dto) {
        boolean hasBasic = StringUtils.hasText(dto.getLegacyBasicAuthUrl()) && StringUtils.hasText(dto.getLegacyBasicUser()) && StringUtils.hasText(dto.getLegacyBasicPsw());
        boolean hasJwt = StringUtils.hasText(dto.getLegacyJwtKid()) && StringUtils.hasText(dto.getLegacyJwtSubject()) && StringUtils.hasText(dto.getLegacyJwtIssuer()) && StringUtils.hasText(dto.getLegacyJwtAlgorithm()) && StringUtils.hasText(dto.getLegacyJwtSigningKey());

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
}
