package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public interface OrganizationService {
    OrganizationDTO getOrganizationInfo(String ipaCode);
}
