package it.gov.pagopa.payhub.activities.dto.debtposition;

import it.gov.pagopa.payhub.activities.dto.CartDTO;
import it.gov.pagopa.payhub.activities.dto.ReceiptDTO;
import it.gov.pagopa.payhub.activities.dto.TransferDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
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
    private LocalDate creationDate;
    private LocalDate updateDate;
    private LocalDate dueDate;
    private String uniqueIdentifierTypePayer;
    private LocalDate paymentExecutionDate;
    private String paymentTypeCode;
    private String singlePaymentAmount;
    private String paCommissionFee;
    private String paymentReason;
    private String collectionSpecificDetailsSinglePayment; //riscossione
    private LocalDate iuvCreationDate;
    private String displayedPaymentReason;
    private String balance; //XML
    private boolean flagGenerateIuv;
    private String sessionId;
    private boolean flagIuvVolatile;
    private String payerUniquePaymentIdHash;
    private String personalDataId;
    private List<TransferDTO> transferDTOS;
    private ReceiptDTO receiptDTO;
}