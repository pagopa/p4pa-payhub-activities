package it.gov.pagopa.payhub.activities.activity.debtposition.aca;

import it.gov.pagopa.payhub.activities.connector.aca.AcaService;
import it.gov.pagopa.payhub.activities.connector.aca.mapper.DebtPositionDTOMapper;
import it.gov.pagopa.pu.debtposition.dto.generated.IupdSyncStatusUpdateDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


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
        List<String> iudList = acaService.createAcaDebtPosition(debtPositionDTO);
        IupdSyncStatusUpdateDTO iupdSyncStatusUpdateDTO = IupdSyncStatusUpdateDTO.builder()
                .newStatus(IupdSyncStatusUpdateDTO.NewStatusEnum.UNPAID)
                .iupdPagopa(null)
                .build();
        return iudList.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        iud -> iupdSyncStatusUpdateDTO));
    }
}
