package it.gov.pagopa.payhub.activities.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PairDeserializer extends JsonDeserializer<Pair> {

  @Override
  public Pair deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    final ObjectMapper mapper = (ObjectMapper) p.getCodec();
    Object left = mapper.treeToValue(node.get("left"), Object.class);
    Object right = mapper.treeToValue(node.get("right"), Object.class);
    return Pair.of(left, right);
  }
}
