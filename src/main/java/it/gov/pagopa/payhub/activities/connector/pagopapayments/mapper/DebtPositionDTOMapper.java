package it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.DebtPositionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PaymentOptionDTOMapper.class})
public interface DebtPositionDTOMapper {

  DebtPositionDTO map(it.gov.pagopa.pu.debtposition.dto.generated.DebtPositionDTO debtPositionDTO);
}
