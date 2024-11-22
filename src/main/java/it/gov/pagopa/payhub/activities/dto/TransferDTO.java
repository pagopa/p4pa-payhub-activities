package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferDTO implements Serializable {

    private OrganizationDTO org;
    private String fiscalCode;
    private String beneficiaryName;
    private String creditIban;
    private String beneficiaryAddress;
    private String beneficiaryCivic;
    private String beneficiaryPostalCode;
    private String beneficiaryLocation;
    private String beneficiaryProvince;
    private String beneficiaryNation;
    private String secondaryAmount;
    private LocalDate creationDate;
    private LocalDate updateDate;
    private String paymentContextCode;
    private String paymentReasonMultiBeneficiary;
    private InstallmentDTO installment;
    private String stamp;
    private String stampType;
    private String category;
    private String collectionSpecificDetailsSinglePayment; //riscossione esiste su InstallmentDTO

}
