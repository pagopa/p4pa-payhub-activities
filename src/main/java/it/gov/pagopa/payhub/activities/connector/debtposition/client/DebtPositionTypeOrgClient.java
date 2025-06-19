package it.gov.pagopa.payhub.activities.connector.debtposition.client;

import it.gov.pagopa.payhub.activities.connector.debtposition.config.DebtPositionApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrg;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionTypeOrgRequestBody;
import it.gov.pagopa.pu.debtposition.dto.generated.IONotificationDTO;
import it.gov.pagopa.pu.workflowhub.dto.generated.PaymentEventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Lazy
@Service
@Slf4j
public class DebtPositionTypeOrgClient {

    private final DebtPositionApisHolder debtPositionApisHolder;

    public DebtPositionTypeOrgClient(DebtPositionApisHolder debtPositionApisHolder) {
        this.debtPositionApisHolder = debtPositionApisHolder;
    }

    public DebtPositionTypeOrg findById(Long debtPositionTypeOrgId, String accessToken) {
        try {
            return debtPositionApisHolder.getDebtPositionTypeOrgEntityApi(accessToken)
                    .crudGetDebtpositiontypeorg(String.valueOf(debtPositionTypeOrgId));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find DebtPositionTypeOrg having id {}", debtPositionTypeOrgId);
            return null;
        }
    }

    public IONotificationDTO getIONotificationDetails(Long debtPositionTypeOrgId, PaymentEventType paymentEventType, String accessToken) {
        try{
            return debtPositionApisHolder.getDebtPositionTypeOrgApi(accessToken)
                    .getIONotificationDetails(debtPositionTypeOrgId, it.gov.pagopa.pu.debtposition.dto.generated.PaymentEventType.valueOf(paymentEventType.getValue()));
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find IONotificationDetails related to DebtPosition having id {} and paymentEventType {}", debtPositionTypeOrgId, paymentEventType);
            return null;
        }
    }

    public DebtPositionTypeOrg getDebtPositionTypeOrgByInstallmentId(Long installmentId, String accessToken) {
        try {
            return debtPositionApisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
                    .crudDebtPositionTypeOrgsGetDebtPositionTypeOrgByInstallmentId(installmentId);
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find DebtPositionTypeOrg having installmentId {}", installmentId);
            return null;
        }
    }


    public DebtPositionTypeOrg getDebtPositionTypeOrgByOrganizationIdAndCode(Long organizationId, String code, String accessToken) {
        try {
            return debtPositionApisHolder.getDebtPositionTypeOrgSearchControllerApi(accessToken)
                    .crudDebtPositionTypeOrgsFindByOrganizationIdAndCode(organizationId, code);
        } catch (HttpClientErrorException.NotFound e){
            log.info("Cannot find DebtPositionTypeOrg having organization id {} and debt position type code {}", organizationId, code);
            return null;
        }
    }

    public DebtPositionTypeOrg createDebtPositionTypeOrg(DebtPositionTypeOrgRequestBody debtPositionTypeOrgRequestBody, String accessToken) {
        return debtPositionApisHolder.getDebtPositionTypeOrgEntityApi(accessToken)
                .crudCreateDebtpositiontypeorg(debtPositionTypeOrgRequestBody);
    }

}
