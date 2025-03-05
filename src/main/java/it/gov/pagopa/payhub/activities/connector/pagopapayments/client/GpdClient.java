package it.gov.pagopa.payhub.activities.connector.pagopapayments.client;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.config.PagoPaPaymentsApisHolder;
import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class GpdClient {

    private final PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder;

    public GpdClient(PagoPaPaymentsApisHolder pagoPaPaymentsApisHolder) {
        this.pagoPaPaymentsApisHolder = pagoPaPaymentsApisHolder;
    }

    public void syncGpd(String iud, DebtPositionDTO debtPositionDTO, String accessToken) {
        pagoPaPaymentsApisHolder.getGpdApi(accessToken).syncGpd(iud, debtPositionDTO);
    }
}
