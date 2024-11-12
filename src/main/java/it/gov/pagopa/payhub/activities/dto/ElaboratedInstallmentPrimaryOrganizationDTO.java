package it.gov.pagopa.payhub.activities.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElaboratedInstallmentPrimaryOrganizationDTO implements Serializable {

    private String beneficiaryName;
    private String uniqueIdentificationCode;
    private String debitIban;
    private String amount;
    private String beneficiaryAddress;
    private String beneficiaryCivic;
    private String beneficiaryPostalCode;
    private String beneficiaryNation;
    private String beneficiaryProvince;
    private String beneficiaryLocation;

    @JsonIgnore
    private Long elaboratedInstallmentId;

}