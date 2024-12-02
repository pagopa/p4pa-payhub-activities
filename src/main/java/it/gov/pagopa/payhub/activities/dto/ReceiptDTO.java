package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO implements Serializable {

    private Long receiptId;
    private Instant creationDate;
    private String paymentReceiptId;
    private String noticeNumber;
    private String fiscalCode;
    private String outcome;
    private String creditorReferenceId;
    private Long paymentAmount;
    private String description;
    private String companyName;
    private String officeName;

    //*from CtSubject debtor
    private PersonDTO debtor;

    private String idPsp;
    private String pspFiscalCode;
    private String pspPartitaIva;
    private String pspCompanyName;
    private String idChannel;
    private String channelDescription;

    //*from CtSubject payer
    private PersonDTO payer;

    private String paymentMethod;
    private Long fee;
    private LocalDate paymentDateTime;
    private LocalDate applicationDate;
    private LocalDate transferDate;

    private byte[] receiptBytes;
    private boolean standin;
    private String status;
    private Instant dtProcessing;
    private Integer numTriesProcessing;
}
