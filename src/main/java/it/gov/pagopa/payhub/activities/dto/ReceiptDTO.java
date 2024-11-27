package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO implements Serializable {

    private Long id;
    private Date creationDate;
    private ElaboratedInstallmentDTO elaboratedInstallmentDTO;
    private String receiptId;
    private String noticeNumber;
    private String fiscalCode;
    private String outcome;
    private String creditorReferenceId;
    private BigDecimal paymentAmount;
    private String description;
    private String companyName;
    private String officeName;
    //*from CtSubject debtor
    private String uniqueIdentifierTypeDebtor;

    private String idPsp;
    private String pspFiscalCode;
    private String pspPartitaIva;
    private String pspCompanyName;
    private String idChannel;
    private String channelDescription;
    //*from CtSubject payer
    private String uniqueIdentifierTypePayer;

    private String paymentMethod;
    private BigDecimal fee;
    private Date paymentDateTime;
    private Date applicationDate;
    private Date transferDate;
    //from CtTransferListPA list ( two items)
    private BigDecimal transferAmount1;
    private String fiscalCodePa1;
    private String iban1;
    private String remittanceInformation1;
    private String transferCategory1;
    private BigDecimal transferAmount2;
    private String fiscalCodePa2;
    private String iban2;
    private String remittanceInformation2;
    private String transferCategory2;

    private Date dtLastExport;
    private int numTryExport;
    private char statusExport;
    private boolean hasReceiptBytes;
    private boolean standin;
    private String status;
    private Date dtProcessing;
    private Integer numTriesProcessing;

    private Long personalDataId;
}
