package it.gov.pagopa.payhub.activities.service;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationServiceImpl implements UserAuthorizationService {
    public UserInfoDTO getUserInfo(String mappedExternalUserId) {
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setMappedExternalUserId(mappedExternalUserId);
        return userInfoDTO;
    }
}
