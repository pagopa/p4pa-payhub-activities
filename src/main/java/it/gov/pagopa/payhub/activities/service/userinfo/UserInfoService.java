package it.gov.pagopa.payhub.activities.service.userinfo;

import it.gov.pagopa.pu.p4paauth.controller.generated.AuthnApi;
import it.gov.pagopa.pu.p4paauth.model.generated.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class UserInfoService {
    private final AuthnApi authnApi;

    public UserInfoService(AuthnApi authnApi) {
        this.authnApi = authnApi;
    }

    public UserInfo getUserInfo(){
        return authnApi.getUserInfo();
    }

}
