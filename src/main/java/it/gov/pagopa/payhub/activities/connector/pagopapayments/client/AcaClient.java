package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class AcaClient {

    private final PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

    public AcaClient(PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder) {
        this.pagoPaPaymentsApisHolder = pagoPaPaymentsApisHolder;
    }

    public void syncAca(String iud, DebtPositionDTO debtPositionDTO, String accessToken) {
        pagoPaPaymentsApisHolder.getAcaApi(accessToken).syncAca(iud, debtPositionDTO);
    }
}
