package it.gov.pagopa.payhub.activities.mapper;

import it.gov.pagopa.pu.pagopapayments.dto.generated.PersonDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PersonDTOMapper {

  PersonDTO map(it.gov.pagopa.pu.debtposition.dto.generated.PersonDTO personDTO);
}
