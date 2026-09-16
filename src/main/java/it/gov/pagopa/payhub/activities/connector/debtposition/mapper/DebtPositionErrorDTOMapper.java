package it.gov.pagopa.payhub.activities.connector.debtposition.mapper;

import it.gov.pagopa.pu.debtpositions.dto.generated.DebtPositionErrorDTO;
import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;

public class DebtPositionErrorDTOMapper {

  private DebtPositionErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(DebtPositionErrorDTO errorDTO) {
    return new PuErrorDTO(
      errorDTO.getCategory().getValue(),
      errorDTO.getCode(),
      errorDTO.getMessage(),
      errorDTO.getFields() != null
        ? errorDTO.getFields().stream()
        .map(field -> new PuErrorDTO.ErrorFieldDTO(
          field.getField(),
          field.getError(),
          field.getMessage()
        ))
        .toList()
        : null
    );
  }
}
