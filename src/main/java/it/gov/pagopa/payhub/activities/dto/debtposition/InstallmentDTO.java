package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.CartDTO;
import it.gov.pagopa.payhub.activities.dto.ReceiptDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentDTO implements Serializable {

    private Long installmentId;
    private boolean flagCurrentInstallment;
    private String status; // anagrafica stato
    private CartDTO cart;
    private String iud;
    private String iuv;
    private Date creationDate;
    private Date updateDate;
    private Date dueDate;
    private Character uniqueIdentifierTypePayer;
    private Date paymentExecutionDate;
    private String paymentTypeCode;
    private BigDecimal singlePaymentAmount;
    private BigDecimal paCommissionFee;
    private String paymentReason;
    private String collectionSpecificDetailsSinglePayment; //riscossione
    private Date iuvCreationDate;
    private String displayedPaymentReason;
    private String balance; //XML
    private boolean flagGenerateIuv;
    private String sessionId;
    private boolean flagIuvVolatile;
    private byte [] payerUniquePaymentIdHash;
    private Long personalDataId;
    private List<TransferDTO> transfers;
    private ReceiptDTO receipt;
}