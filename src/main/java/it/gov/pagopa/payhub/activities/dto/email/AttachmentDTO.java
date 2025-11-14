package it.gov.pagopa.payhub.activities.dto.email;

import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class AttachmentDTO {
  private File file;
  private String fileName;
}
