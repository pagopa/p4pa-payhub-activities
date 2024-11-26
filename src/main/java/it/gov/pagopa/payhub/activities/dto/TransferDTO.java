package it.gov.pagopa.payhub.activities.dto;

import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
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
public class TransferDTO implements Serializable {

    private OrganizationDTO org;
    private String orgFiscalCode;
    private String beneficiaryName;
    private String creditIban;
    private String beneficiaryAddress;
    private String beneficiaryCivic;
    private String beneficiaryPostalCode;
    private String beneficiaryLocation;
    private String beneficiaryProvince;
    private String beneficiaryNation;
    private BigDecimal secondaryAmount;
    private Date creationDate;
    private Date lastUpdateDate;
    private String paymentReasonMultiBeneficiary;
    private InstallmentDTO installment;
    private String stamp;
    private String stampType;
    private String category;
    private String collectionSpecificDetailsSinglePayment; //riscossione esiste su InstallmentDTO

}
