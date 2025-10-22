package it.gov.pagopa.payhub.activities.config.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PairDeserializer extends JsonDeserializer<Pair> {

  @Override
  public Pair deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    JsonNode node = p.getCodec().readTree(p);
    Object left = ctxt.findNonContextualValueDeserializer(ctxt.constructType(Object.class)).deserialize(node.get("left").traverse(p.getCodec()), ctxt);
    Object right = ctxt.findNonContextualValueDeserializer(ctxt.constructType(Object.class)).deserialize(node.get("right").traverse(p.getCodec()), ctxt);
    return Pair.of(left, right);
  }
}
