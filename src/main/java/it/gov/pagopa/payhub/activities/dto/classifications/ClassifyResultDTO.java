package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClassifyResultDTO {
	private Long organizationId;
	private String creditorReferenceId;
	private String regulationUniqueIdentifier;
	private int transferIndex;
}
