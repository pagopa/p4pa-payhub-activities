package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserAuthorizationService {
    UserInfoDTO getUserInfo(String mappedExternalUserId);
}
