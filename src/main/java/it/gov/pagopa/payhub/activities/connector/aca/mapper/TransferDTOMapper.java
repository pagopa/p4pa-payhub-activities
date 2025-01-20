package it.gov.pagopa.payhub.activities.connector.aca.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.TransferDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferDTOMapper {

  TransferDTO map(it.gov.pagopa.pu.debtposition.dto.generated.TransferDTO transferDTO);
}

