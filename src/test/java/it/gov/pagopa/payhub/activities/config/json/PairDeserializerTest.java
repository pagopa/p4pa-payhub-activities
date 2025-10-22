package it.gov.pagopa.payhub.activities.config.json;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PairDeserializerTest {
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    SimpleModule pairModule = new SimpleModule();
    pairModule.addDeserializer(Pair.class, new PairDeserializer());
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(pairModule);
  }

  @Test
  void givenJsonWhenDeserializeThenOk() throws IOException {
    // Given
    String json = "{\"left\":\"ValueA\",\"right\":987}";
    // When
    Pair<?, ?> actualPair = objectMapper.readValue(json, Pair.class);
    // Then
    assertEquals("ValueA", actualPair.getLeft());
    assertEquals(987, actualPair.getRight());
  }
}