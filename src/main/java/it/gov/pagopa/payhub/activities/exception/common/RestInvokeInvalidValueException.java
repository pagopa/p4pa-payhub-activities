package it.gov.pagopa.payhub.activities.exception.common;

import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class RestInvokeInvalidValueException extends InvalidValueException implements RestInvokeHttpClientException {

  private final String applicationName;
  private final HttpStatus httpStatus;
  private final String category;

  public RestInvokeInvalidValueException(String applicationName, HttpStatus httpStatus, String category, String code, String message, List<PuErrorDTO.ErrorFieldDTO> fields) {
    super(code, message, fields);
    this.applicationName = applicationName;
    this.httpStatus = httpStatus;
    this.category = category;
  }
}
