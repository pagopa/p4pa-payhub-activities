package it.gov.pagopa.payhub.activities.utility;

import it.gov.pagopa.payhub.activities.dto.UserInfoDTO;

public interface UserAuthorizationActivity {
    UserInfoDTO getUserInfo(String mappedExternalUserId);
}
