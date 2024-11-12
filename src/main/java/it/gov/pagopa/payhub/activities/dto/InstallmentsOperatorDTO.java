package it.gov.pagopa.payhub.activities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InstallmentsOperatorDTO implements Serializable {

    private Long installmentId;
    private String uniqueIdentificationCode;
    private String iud;
    private String iuv;
    private String remittanceInformation;
    private String displayedRemittanceInformation;
    private String amount;
    private LocalDate dueDate;
    private String status;
    private String statusCode;
    private LocalDateTime statusDate;
    private boolean hasNotice;
    private boolean hasReceipt;

    //details
    private OrganizationTypeInstallmentDTO organizationTypeInstallment;
    private String beneficiaryName;
    private String subjectType;
    private boolean flagAnonymousData;
    private boolean hasFiscalCode;
    private String email;
    private String address;
    private String civic;
    private String postalCode;
    private NationDTO nation;
    private ProvinceDTO province;
    private CityDTO municipality;
    private boolean flagGenerateIuv;
    private String iuf;

    //details elaborated installment
    private LocalDateTime transactionStartDate;
    private String transactionId;
    private String holder;
    private String selectedPSP;

    private String installmentType; // "debito" or "pagato"
    private String invalidDescription; // Message thrown by ValidatorException when insertion, update.

    private boolean flagMultiBeneficiary;
    private boolean flagIuvVolatile;

    //primary org detail
    private InstallmentPrimaryOrganizationDTO installmentPrimaryOrgDetail;
    private ElaboratedInstallmentPrimaryOrganizationDTO elaboratedInstallmentPrimaryOrgDetail;

    //multi beneficiary detail
    private MultiBeneficiaryInstallmentDTO multiBeneficiaryInstallmentDetail;
    private ElaboratedMultiBeneficiaryInstallmentDTO elaboratedMultiBeneficiaryInstallmentDetail;

}