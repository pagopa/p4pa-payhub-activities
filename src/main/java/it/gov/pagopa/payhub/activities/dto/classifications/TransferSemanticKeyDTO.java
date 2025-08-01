package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TransferSemanticKeyDTO {
	private Long orgId;
	private String iuv;
	private String iur;
	private Integer transferIndex;
}
