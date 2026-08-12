package it.gov.pagopa.payhub.activities.connector.organization.mapper;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.pu.organization.dto.generated.OrganizationErrorDTO;

public class OrganizationErrorDTOMapper {

  private OrganizationErrorDTOMapper() {
    /* This utility class should not be instantiated */
  }


  public static PuErrorDTO map(OrganizationErrorDTO errorDTO) {
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
