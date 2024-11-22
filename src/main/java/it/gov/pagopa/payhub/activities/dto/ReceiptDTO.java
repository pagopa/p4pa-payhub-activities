package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
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
    private String uniqueIdentifierValueDebtor;
    private String fullNameDebtor;
    private String streetNameDebtor;
    private String civicNumberDebtor;
    private String postalCodeDebtor;
    private String cityDebtor;
    private String stateProvinceRegionDebtor;
    private String countryDebtor;
    private String emailDebtor;

    private String idPsp;
    private String pspFiscalCode;
    private String pspPartitaIva;
    private String pspCompanyName;
    private String idChannel;


    //*from CtSubject payer
    private String uniqueIdentifierTypePayer;
    private String uniqueIdentifierValuePayer;
    private String fullNamePayer;
    private String streetNamePayer;
    private String civicNumberPayer;
    private String postalCodePayer;
    private String cityPayer;
    private String stateProvinceRegionPayer;
    private String countryPayer;
    private String emailPayer;

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
    private byte[] receiptBytes;
    private boolean standIn;
    private String status;
    private Date dtProcessing;
    private Integer numTriesProcessing;
}
