package it.gov.pagopa.payhub.activities.connector.auth.mapper;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.pu.auth.dto.generated.AuthErrorDTO;

public class AuthErrorDTOMapper {

  private AuthErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(AuthErrorDTO errorDTO) {
    return new PuErrorDTO(
      errorDTO.getError().getValue(),
      errorDTO.getCode(),
      errorDTO.getErrorDescription(),
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
