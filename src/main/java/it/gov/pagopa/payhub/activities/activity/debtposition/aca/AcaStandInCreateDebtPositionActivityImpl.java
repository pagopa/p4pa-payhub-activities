package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.aca.AcaService;
import it.gov.pagopa.payhub.activities.connector.aca.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;


@Lazy
@Service
public class AcaStandInCreateDebtPositionActivityImpl implements AcaStandInCreateDebtPositionActivity {

    private final AcaService acaService;
    private final DebtPositionDTOMapper debtPositionDTOMapper;

    public AcaStandInCreateDebtPositionActivityImpl(AcaService acaService, DebtPositionDTOMapper debtPositionDTOMapper) {
        this.acaService = acaService;
        this.debtPositionDTOMapper = debtPositionDTOMapper;
    }

    @Override
    public Map<String, IupdSyncStatusUpdateDTO> createAcaDebtPosition(it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO debtPosition) {
        DebtPositionDTO debtPositionDTO = debtPositionDTOMapper.map(debtPosition);
        acaService.createAcaDebtPosition(debtPositionDTO);
        return Map.of(); //TODO
    }
}
