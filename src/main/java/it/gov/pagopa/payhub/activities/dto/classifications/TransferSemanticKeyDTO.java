package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferSemanticKeyDTO {
	private Long orgId;
	private String iuv;
	private String iur;
	private int transferIndex;
}
