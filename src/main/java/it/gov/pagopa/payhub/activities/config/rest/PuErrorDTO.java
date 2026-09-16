package it.gov.pagopa.payhub.activities.config.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorFieldDTO {

        private String field;
        private String error;
        private String message;
    }
}
