package it.gov.pagopa.payhub.activities.dto.classifications;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatePaymentsReportingCheckDTO {
	private Long orgId;
	private String iuv;
	private Integer transferIndex;
	private Long amount;
	private String orgFiscalCode;
}
