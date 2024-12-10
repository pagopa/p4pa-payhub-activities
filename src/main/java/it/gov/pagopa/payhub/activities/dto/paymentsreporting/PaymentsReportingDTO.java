package it.gov.pagopa.payhub.activities.dto.paymentsreporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsReportingDTO {

	private Long paymentsReportingId;
	private int version;
	private Instant creationDate;
	private Instant lastUpdateDate;
	private Long organizationId;
	private Long ingestionFlowFileId;
	private String pspIdentifier;
	private String flowIdentifierCode;
	private LocalDate flowDateTime;
	private String regulationUniqueIdentifier;
	private LocalDate regulationDate;
	private String senderPspType;
	private String senderPspCode;
	private String senderPspName;
	private String receiverOrganizationType;
	private String receiverOrganizationCode;
	private String receiverOrganizationName;
	private long totalPayments;
	private Long totalAmountCents;
	private String creditorReferenceId;
	private String regulationId;
	private Long amountPaidCents;
	private String paymentOutcomeCode;
	private LocalDate payDate;
	private LocalDate acquiringDate;
	private int transferIndex;
	private String bicCodePouringBank;
}
