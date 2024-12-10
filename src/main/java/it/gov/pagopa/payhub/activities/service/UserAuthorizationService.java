package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.pu.p4paauth.dto.generated.UserInfo;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public interface UserAuthorizationService {
    UserInfo getUserInfo(String ipaCode, String mappedExternalUserId);
}
