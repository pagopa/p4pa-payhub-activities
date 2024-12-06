package it.gov.pagopa.payhub.activities.dto.paymentsreporting;

import it.gov.pagopa.payhub.activities.dto.IngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.dto.OrganizationDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingDTO {

	private int version;
	private Timestamp creationDate;
	private Timestamp lastChangeDate;
	private OrganizationDTO orgId;
	private IngestionFlowFileDTO ingestionFlowFile;
	private String idPsp;
	private String flowIdentifierCode;
	private Date flowDateTime;
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
