package it.gov.pagopa.payhub.activities.dto.reportingflow;

import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingDTO {

	private int version;
	private Timestamp creationDate;
	private Timestamp lastChangeDate;
	private OrganizationDTO orgId;
	private IngestionFlowFileDTO ingestionFlowId;
	private String idPsp;
	private String flowIdentifierCode;
	private Timestamp flowDateTime;
	private String regulationUniqueIdentifier;
	private Date regulationDate;
	private String senderPspType;
	private String senderPspCode;
	private String senderPspName;
	private String receiverOrganizationType;
	private String receiverOrganizationId;
	private String receiverOrganizationName;
	private long totalPayments;
	private BigDecimal sumPayments;
	private String creditorReferenceId;
	private String regulationId;
	private BigDecimal amountPaid;
	private String paymentOutcomeCode;
	private Date payDate;
	private Date acquiringDate;
	private int transferIndex;
	private String bicCodePouringBank;
}
