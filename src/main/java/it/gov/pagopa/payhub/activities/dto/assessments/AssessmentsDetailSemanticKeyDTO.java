package it.gov.pagopa.payhub.activities.dto.assessments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentsDetailSemanticKeyDTO {
	private Long orgId;
	private String iuv;
	private String iud;
}
