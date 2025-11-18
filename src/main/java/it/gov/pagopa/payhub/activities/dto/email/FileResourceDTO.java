package it.gov.pagopa.payhub.activities.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class FileResourceDTO {
  private Resource resource;
  private String fileName;
}
