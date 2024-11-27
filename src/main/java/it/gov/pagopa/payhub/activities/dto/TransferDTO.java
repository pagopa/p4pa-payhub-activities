package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO implements Serializable {

    private Long transferId;
    private String orgFiscalCode;
    private String beneficiaryName;
    private String iban;
    private String beneficiaryAddress;
    private String beneficiaryCivic;
    private String beneficiaryPostalCode;
    private String beneficiaryLocation;
    private String beneficiaryProvince;
    private String beneficiaryNation;
    private Long amount;
    private Instant creationDate;
    private Instant lastUpdateDate;
    private String remittanceInformation;
    private String stamp;
    private String stampType;
    private String documentHash;
    private String category;
    private String status;
    private Integer transferIndex;
}
