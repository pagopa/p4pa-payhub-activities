package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.InstallmentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TransferDTOMapper.class, PersonDTOMapper.class})
public interface InstallmentDTOMapper {

  InstallmentDTO map(it.gov.pagopa.pu.debtposition.dto.generated.InstallmentDTO installmentDTO);
}
