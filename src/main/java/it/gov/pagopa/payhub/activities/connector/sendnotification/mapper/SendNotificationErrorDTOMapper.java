package it.gov.pagopa.payhub.activities.connector.sendnotification.mapper;

import it.gov.pagopa.pu.sendnotification.dto.generated.SendNotificationErrorDTO;
import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;

public class SendNotificationErrorDTOMapper {

  private SendNotificationErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(SendNotificationErrorDTO errorDTO) {
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
