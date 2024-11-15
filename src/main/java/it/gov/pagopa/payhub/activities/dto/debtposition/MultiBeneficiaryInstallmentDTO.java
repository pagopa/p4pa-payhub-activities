package it.gov.pagopa.payhub.activities.dto.debtposition;

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
public class MultiBeneficiaryInstallmentDTO implements Serializable {

    private String beneficiaryName;
    private String uniqueIdentificationCode;
    private String debitIban;
    private String beneficiaryAddress;
    private String beneficiaryCivic;
    private String beneficiaryPostalCode;
    private String beneficiaryNation;
    private String beneficiaryProvince;
    private String beneficiaryLocation;
    private String secondaryAmount;
    private String remittanceInformationMultiBeneficiary;
    private String orgInstallmentTypeCode;

    @JsonIgnore
    private Long installmentId;
}