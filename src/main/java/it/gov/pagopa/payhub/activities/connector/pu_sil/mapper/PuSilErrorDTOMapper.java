package it.gov.pagopa.payhub.activities.connector.pu_sil.mapper;

import it.gov.pagopa.pu.pusil.dto.generated.PuSilErrorDTO;
import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;

public class PuSilErrorDTOMapper {

  private PuSilErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(PuSilErrorDTO errorDTO) {
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
