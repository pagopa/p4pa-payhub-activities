package it.gov.pagopa.payhub.activities.connector.pagopapayments.mapper;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.pu.pagopapayments.dto.generated.PagoPaPaymentsErrorDTO;

public class PagoPaPaymentsErrorDTOMapper {

  private PagoPaPaymentsErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(PagoPaPaymentsErrorDTO errorDTO) {
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
