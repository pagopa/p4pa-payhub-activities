package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.aca.AcaService;
import it.gov.pagopa.payhub.activities.connector.aca.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
public class AcaStandInActivityImpl implements AcaStandInActivity {

    private final AcaService acaService;
    private final DebtPositionDTOMapper debtPositionDTOMapper;

    public AcaStandInActivityImpl(AcaService acaService, DebtPositionDTOMapper debtPositionDTOMapper) {
        this.acaService = acaService;
        this.debtPositionDTOMapper = debtPositionDTOMapper;
    }

    @Override
    public void createAcaDebtPosition(it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO debtPosition) {
        DebtPositionDTO debtPositionDTO = debtPositionDTOMapper.map(debtPosition);
        acaService.createAcaDebtPosition(debtPositionDTO);
    }
}
