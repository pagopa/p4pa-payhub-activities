package it.gov.pagopa.payhub.activities.dto.exportflow;

import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFileStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequest {
  private Long exportFileId;
  private ExportFileStatus oldStatus;
  private ExportFileStatus newStatus;
  private String filePathName;
  private String fileName;
  private Long fileSize;
  private Long exportedRows;
  private String errorDescription;
}
