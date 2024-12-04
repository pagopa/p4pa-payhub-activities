package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {
    public UserInfoDTO getUserInfo(String mappedExternalUserId) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setMappedExternalUserId(mappedExternalUserId);
        userInfoDTO.setEmail("testmail@mail.com");
        return userInfoDTO;
    }
}
