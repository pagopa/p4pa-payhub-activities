package it.gov.pagopa.payhub.activities.service.auth;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserAuthorizationService {
    UserInfoDTO getUserInfoDTO(String mappedExternalUserId);
}
