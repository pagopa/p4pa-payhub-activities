package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElaboratedInstallmentDTO {

    private Long elaboratedInstallmentId;
    private int version;
    private boolean flagCurrentInstallment;
    private FlowDTO flow;
    private long numberLineFlow;
    private String status; //anagrafica stato
    private CartDTO cart;
    private String codeIud;
    private String codeIuv;
    private String codeAckRp;
    private Date creationDate;
    private Date rpLastUpdateDate;
    private Date outcomeLastUpdateDate;
    private String codeSilSendPaymentRequestIdPsp;
    private String codeSilSendPaymentRequestIdIntermediaryPsp;
    private String codeSilSendPaymentRequestIdChannel;
    private String codeSilSendPaymentRequestIdDomain;
    private String codeSilSendPaymentRequestUniquePaymentId;
    private String codeSilSendPaymentRequestPaymentContextCode;
    private String silSendPaymentRequestOutcome;
    private Integer codeSilSendPaymentRequestRedirect;
    private String codeSilSendPaymentRequestUrl;
    private String codeSilSendPaymentRequestFaultCode;
    private String silSendPaymentRequestFaultString;
    private String codeSilSendPaymentRequestId;
    private String silSendPaymentRequestDescription;
    private Integer codeSilSendPaymentRequestSerial;
    private String objectVersion;
    private String domainId;
    private String applicantStationId;
    private String messageRequest;
    private Date messageRequestDate;
    private String authenticationSubject;
    private Character remitterPaymentRequestUniqueIdType;
    private byte[] remitterPaymentRequestUniqueHash;
    private char payerPaymentRequestUniqueType;
    private byte[] payerPaymentRequestUniqueTypeHash;
    private Date paymentExecutionDate;
    private BigDecimal totalAmountToPay;
    private String paymentType;
    private String remitterUniquePaymentId;
    private String rpUniquePaymentId;
    private String debitIban;
    private BigDecimal singlePaymentAmount;
    private BigDecimal paCommissionFee;
    private String accreditIban;
    private String supportIban;
    private String payerCredentials;
    private String paymentReason;
    private String collectionSpecificDetailsSinglePayment;
    private String silSendOutcomeDomainId;
    private String silSendOutcomePaymentId;
    private String silSendOutcomePaymentContextCode;
    private String silSendOutcome;
    private String silSendOutcomeFaultCode;
    private String silSendOutcomeFaultString;
    private String silSendOutcomeId;
    private String silSendOutcomeDescription;
    private Integer silSendOutcomeSerial;
    private String outcomeSubjectVersion;
    private String domainIdCode;
    private String requestingStationId;
    private String receiptMessageId;
    private Date receiptMessageDate;
    private String requestMessageRef;
    private Date requestMessageRefDate;
    private Character attestingInstituteUniqueCodeId;
    private String attestingInstituteUniqueCode;
    private String attestingInstituteName;
    private String attestingInstituteUnitOperator;
    private String attestingInstituteUnitOperatorName;
    private String attestingInstituteAddress;
    private String attestingInstituteCivic;
    private String attestingInstitutePostalCode;
    private String attestingInstituteLocation;
    private String attestingInstituteProvince;
    private String attestingInstituteNation;
    private Character beneficiaryOrgTypeId;
    private String beneficiaryOrgUniqueCode;
    private String beneficiaryOrgName;
    private String beneficiaryOrgCodeUnitOperator;
    private String beneficiaryOrgCodeUnitOperatorName;
    private String beneficiaryOrgAddress;
    private String beneficiaryOrgCivic;
    private String beneficiaryOrgPostalCode;
    private String beneficiaryOrgLocation;
    private String beneficiaryOrgProvince;
    private String beneficiaryOrgNation;
    private Character remitterIdUniqueType; //versante
    private Character payerIdUniqueType;
    private Character payerPaymentOutcomeCode;
    private BigDecimal totalPaidAmount;
    private String payerUniquePaymentId;
    private String payerPaymentContextCode;
    private BigDecimal payerSinglePaidAmount;
    private String payerSinglePaidAmountOutcome;
    private Date payerSinglePaidAmountOutcomeDate;
    private String payerSinglePaidAmountCollectionId;  //riscossione
    private String payerPaymentReason;
    private String payerSinglePaidAmountSpecificCollection;  //riscossione
    private String installmentTypeCode;
    private Date lastStatusUpdateDate;
    private Integer paymentModel;
    private String orgSilPaymentResponseUrl;
    private String sendRtSignatureType;
    private Integer singlePaymentIndex;
    private BigDecimal pspSinglePaymentAppliedFees;
    private String pspSinglePaymentReceiptAttachmentType;
    private byte[] blbSinglePaymentReceiptAttachmentData;
    private String rpSinglePaymentReceiptStampType;
    private String rpSinglePaymentMbHashDocument;
    private String rpSinglePaymentMbProvinceResidence;
    private String rpSinglePaymentReasonAgid;
    private String balance;
    private String silSendRpOriginalFaultCode;
    private String silSendRpOriginalFaultString;
    private String silSendRpOriginalFaultCodeDescription;
    private String silSendOutcomeOriginalFaultCode;
    private String silSendOutcomeOriginalFaultString;
    private String silSendOutcomeOriginalFaultDescription;
    private String gpdIupd;
    private Character gpdStatus;

    private OrganizationDTO org;
    private Long personalDataId;
}
