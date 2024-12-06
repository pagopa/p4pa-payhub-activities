package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class UserAuthorizationServiceImpl {

    public UserInfoDTO getUserInfo(String ipaCode, String mappedExternalUserId) {
        // to implement with auth utility
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setEmail("test@emailtest.com");
        return userInfoDTO;
    }
}
