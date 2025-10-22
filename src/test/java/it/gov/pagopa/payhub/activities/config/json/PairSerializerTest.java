package it.gov.pagopa.payhub.activities.config.json;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PairSerializerTest {
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
      SimpleModule pairModule = new SimpleModule();
      pairModule.addSerializer(Pair.class, new PairSerializer());
      objectMapper = new ObjectMapper();
      objectMapper.registerModule(pairModule);
    }

    @Test
    void givenPairWhenSerializeThenOk() throws IOException {
      // Given
      Pair<String, Integer> pair = Pair.of("Key1", 123);
      String expectedJson = "{\"left\":\"Key1\",\"right\":123}";

      // When
      String actualJson = objectMapper.writeValueAsString(pair);

      // Then
      assertEquals(expectedJson, actualJson);
    }

    @Test
    void givenPairWithNullWhenSerializeThenOk() throws IOException {
      // Given
      Pair<String, Integer> pair = Pair.of(null, 456);
      String expectedJson = "{\"left\":null,\"right\":456}";

      // When
      String actualJson = objectMapper.writeValueAsString(pair);

      // Then
      assertEquals(expectedJson, actualJson);
    }
}