package it.gov.pagopa.payhub.activities.connector.ionotification.mapper;

import it.gov.pagopa.pu.ionotification.dto.generated.IoNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;

public class IoNotificationErrorDTOMapper {

  private IoNotificationErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(IoNotificationErrorDTO errorDTO) {
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
