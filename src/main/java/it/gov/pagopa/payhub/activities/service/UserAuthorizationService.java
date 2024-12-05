package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public interface UserAuthorizationService {
    UserInfoDTO getUserInfo(String ipaCode, String mappedExternalUserId);
}
