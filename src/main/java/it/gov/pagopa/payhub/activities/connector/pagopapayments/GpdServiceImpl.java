package it.gov.pagopa.payhub.activities.connector.pagopapayments;

import it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
@Slf4j
public class GpdServiceImpl implements GpdService {

    @Override
    public String syncInstallmentGpd(String iud, DebtPositionDTO debtPositionDTO) {
        throw new NotImplementedException(); //TODO P4ADEV-1375 invoke GPD sync API of pagopa-payments
    }
}
