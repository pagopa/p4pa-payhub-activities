package it.gov.pagopa.payhub.activities.connector.processexecutions.mapper;

import it.gov.pagopa.pu.processexecutions.dto.generated.ProcessExecutionsErrorDTO;
import it.gov.pagopa.payhub.activities.config.rest.PuErrorDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

class ProcessExecutionsErrorDTOMapperTest {

  @Test
  void whenMapThenReturnPuErrorDTO() {
    // Given
    ProcessExecutionsErrorDTO errorDTO = TestUtils.getPodamFactory().manufacturePojo(ProcessExecutionsErrorDTO.class);
    Objects.requireNonNull(errorDTO.getFields());

    // When
    PuErrorDTO result = ProcessExecutionsErrorDTOMapper.map(errorDTO);

    // Then
    TestUtils.checkNotNullFields(result);

    Assertions.assertEquals(errorDTO.getCategory().getValue(), result.category());
    Assertions.assertEquals(errorDTO.getCode(), result.code());
    Assertions.assertEquals(errorDTO.getMessage(), result.message());

    Assertions.assertEquals(5, result.fields().size());
    List<PuErrorDTO.ErrorFieldDTO> fields = result.fields();
    for (int i = 0; i < fields.size(); i++) {
      PuErrorDTO.ErrorFieldDTO ef = fields.get(i);
      TestUtils.checkNotNullFields(ef);

      it.gov.pagopa.pu.processexecutions.dto.generated.ErrorFieldDTO expectedErrorFieldDTO = errorDTO.getFields().get(i);
      Assertions.assertEquals(expectedErrorFieldDTO.getField(), ef.getField());
      Assertions.assertEquals(expectedErrorFieldDTO.getError(), ef.getError());
      Assertions.assertEquals(expectedErrorFieldDTO.getMessage(), ef.getMessage());
    }

  }

}
