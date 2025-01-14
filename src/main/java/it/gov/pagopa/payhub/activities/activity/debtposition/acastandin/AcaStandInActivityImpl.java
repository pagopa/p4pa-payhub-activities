package it.gov.pagopa.payhub.activities.activity.debtposition.acastandin;

import it.gov.pagopa.payhub.activities.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.pagopapayments.client.generated.AcaApi;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


@Lazy
@Service
public class AcaStandInActivityImpl implements AcaStandInActivity {

    private final AcaApi acaApi;
    private final DebtPositionDTOMapper debtPositionDTOMapper;

    public AcaStandInActivityImpl(AcaApi acaApi, DebtPositionDTOMapper debtPositionDTOMapper) {
        this.acaApi = acaApi;
        this.debtPositionDTOMapper = debtPositionDTOMapper;
    }

    @Override
    public void createAcaDebtPosition(it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO debtPosition) {
        DebtPositionDTO debtPositionDTO = debtPositionDTOMapper.map(debtPosition);
        acaApi.createAca(debtPositionDTO);
    }
}
