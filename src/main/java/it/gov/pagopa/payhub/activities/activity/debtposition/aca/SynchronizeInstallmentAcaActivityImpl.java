package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.pagopapayments.AcaService;
import it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service
public class SynchronizeInstallmentAcaActivityImpl implements SynchronizeInstallmentAcaActivity {

    private final AcaService acaService;
    private final DebtPositionDTOMapper debtPositionDTOMapper;

    public SynchronizeInstallmentAcaActivityImpl(AcaService acaService, DebtPositionDTOMapper debtPositionDTOMapper) {
        this.acaService = acaService;
        this.debtPositionDTOMapper = debtPositionDTOMapper;
    }

    @Override
    public void synchronizeInstallmentAca(it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO debtPosition, String iud) {
        DebtPositionDTO debtPositionDTO = debtPositionDTOMapper.map(debtPosition);
        acaService.syncInstallmentAca(iud, debtPositionDTO);
    }
}
