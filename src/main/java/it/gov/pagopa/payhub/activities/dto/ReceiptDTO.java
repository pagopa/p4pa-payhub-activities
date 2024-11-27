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

    private Long id;
    private Instant creationDate;
    private String receiptId;
    private String noticeNumber;
    private String fiscalCode;
    private String outcome;
    private String creditorReferenceId;
    private Long paymentAmount;
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
    private String channelDescription;
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
