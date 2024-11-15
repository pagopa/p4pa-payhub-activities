package it.gov.pagopa.payhub.activities.activity;

import it.gov.pagopa.payhub.activities.activity.debtposition.InstallmentsValidationActivity;
import it.gov.pagopa.payhub.activities.dao.FlowDao;
import it.gov.pagopa.payhub.activities.dao.PositionDao;
import it.gov.pagopa.payhub.activities.dto.FlowDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.DebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDTO;
import it.gov.pagopa.payhub.activities.dto.debtposition.InstallmentDebtPositionTypeOrgDTO;
import it.gov.pagopa.payhub.activities.dto.organization.OrganizationDTO;
import it.gov.pagopa.payhub.activities.dto.position.CityDTO;
import it.gov.pagopa.payhub.activities.dto.position.NationDTO;
import it.gov.pagopa.payhub.activities.dto.position.ProvinceDTO;
import it.gov.pagopa.payhub.activities.exception.custom.FlowException;
import it.gov.pagopa.payhub.activities.exception.custom.ValidatorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallmentsValidationActivityTest {

    private InstallmentsValidationActivity installmentsValidationActivity;

    @Mock private PositionDao positionDao;
    @Mock private FlowDao flowDao;
    private static final FlowDTO FLOW_DTO = new FlowDTO(1L, 1, new OrganizationDTO(), "STATUS",
            "IUF", 1L, 1L, new Date(), new Date(), true,
            "OPERATOR", Boolean.TRUE, "FILE_PATH", "FILE_NAME", 1L,
            "", "");

    private static final String TYPE_ORG_CODE = "TYPE_ORG_CODE";
    private static final String BENEFICIARY_NAME = "NAME";
    @BeforeEach
    void init() {
        installmentsValidationActivity = new InstallmentsValidationActivity(flowDao, positionDao);
    }


    @Test
    void givenFlowThenSuccess(){
        Long orgId = 1L;
        List<FlowDTO> flowsDTO = List.of(FLOW_DTO);

        when(flowDao.getFlowsByOrganization(orgId, true)).thenReturn(flowsDTO);

        FlowDTO result = installmentsValidationActivity.validateFlow(orgId, true);
        assertEquals(FLOW_DTO, result);
    }

    @Test
    void givenNoFlowThenThrowFlowException(){
        Long orgId = 1L;
        List<FlowDTO> flowsDTO = List.of();

        when(flowDao.getFlowsByOrganization(orgId, true)).thenReturn(flowsDTO);

        assertThrows(FlowException.class, () -> installmentsValidationActivity.validateFlow(orgId, true));
    }

    @Test
    void givenNullFlowThenThrowFlowException(){
        Long orgId = 1L;

        when(flowDao.getFlowsByOrganization(orgId, true)).thenReturn(null);

        assertThrows(FlowException.class, () -> installmentsValidationActivity.validateFlow(orgId, true));
    }

    @Test
    void givenNullInstallmentDebtPositionTypeOrgThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Organization installment type is mandatory", validatorException.getMessage());
    }

    @Test
    void givenNoInstallmentDebtPositionTypeOrgCodeThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setInstallmentDebtPositionTypeOrg(new InstallmentDebtPositionTypeOrgDTO());
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Organization installment type is mandatory", validatorException.getMessage());
    }

    @Test
    void givenNullBeneficiaryNameInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Beneficiary name is mandatory", validatorException.getMessage());
    }

    @Test
    void givenNotValidEmailInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        installmentDTO.setBeneficiaryName(BENEFICIARY_NAME);
        installmentDTO.setEmail("email.it");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Email is not valid", validatorException.getMessage());
    }

    @Test
    void givenNullRemittanceInfoInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        installmentDTO.setBeneficiaryName(BENEFICIARY_NAME);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Remittance information is mandatory", validatorException.getMessage());
    }

    @Test
    void givenRetroactiveDateInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        installmentDTO.setBeneficiaryName(BENEFICIARY_NAME);
        installmentDTO.setEmail("email@email.it");
        installmentDTO.setRemittanceInformation("REMITTANCE_INFO");

        LocalDate dueDate = LocalDate.of(2024, 11, 11);
        installmentDTO.setDueDate(dueDate);

        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("The due date cannot be retroactive", validatorException.getMessage());
    }

    @Test
    void givenNullDateInstallmentWithMandatoryDateThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        installmentDTO.setBeneficiaryName(BENEFICIARY_NAME);
        installmentDTO.setEmail("email@email.it");
        installmentDTO.setRemittanceInformation("REMITTANCE_INFO");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagMandatoryDueDate(true);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("The due date is mandatory", validatorException.getMessage());
    }

    @Test
    void givenNullDateInstallmentWithNonMandatoryDateThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        InstallmentDebtPositionTypeOrgDTO installmentDebtPositionTypeOrgDTO = new InstallmentDebtPositionTypeOrgDTO();
        installmentDebtPositionTypeOrgDTO.setTypeCode(TYPE_ORG_CODE);
        installmentDTO.setInstallmentDebtPositionTypeOrg(installmentDebtPositionTypeOrgDTO);
        installmentDTO.setBeneficiaryName(BENEFICIARY_NAME);
        installmentDTO.setEmail("email@email.it");
        installmentDTO.setRemittanceInformation("REMITTANCE_INFO");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagMandatoryDueDate(false);

        assertDoesNotThrow(() -> installmentsValidationActivity.formalValidation(installmentDTO, debtPositionTypeOrgDTO));
    }

    @Test
    void givenNullFiscalCodeWithNoAnonymousFlagThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setFlagAnonymousData(false);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagAnonymousFiscalCode(false);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateUniqueIdentificationCode(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Unique identification code is mandatory", validatorException.getMessage());
    }

    @Test
    void givenInstallmentAnonymousWithAnonymousFlagThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setFlagAnonymousData(true);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagAnonymousFiscalCode(true);

        String result = installmentsValidationActivity.validateUniqueIdentificationCode(installmentDTO, debtPositionTypeOrgDTO);
        assertEquals("ANONIMO", result);
    }

    @Test
    void givenInstallmentNotAnonymousThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setUniqueIdentificationCode("FISCAL_CODE");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagAnonymousFiscalCode(true);

        String result = installmentsValidationActivity.validateUniqueIdentificationCode(installmentDTO, debtPositionTypeOrgDTO);
        assertEquals("FISCAL_CODE", result);
    }

    @Test
    void givenInstallmentWithNullIdCodeWithAnonymousFlagThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setFlagAnonymousData(false);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setFlagAnonymousFiscalCode(true);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateUniqueIdentificationCode(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("This organization installment type or installment does not allow an anonymous unique identification code", validatorException.getMessage());
    }

    @Test
    void givenNullAmountInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Amount is mandatory", validatorException.getMessage());
    }

    @Test
    void givenWrongAmountInstallmentWithAmountDebtTypeThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAmount("21.3");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setAmount(new BigDecimal(45));

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Invalid amount for this installment type", validatorException.getMessage());
    }

    @Test
    void givenRightAmountInstallmentWithAmountDebtTypeThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAmount("45.5");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();
        debtPositionTypeOrgDTO.setAmount(new BigDecimal("45.5"));

        assertDoesNotThrow(() -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
    }

    @Test
    void givenNegativeAmountInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAmount("-45.5");
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Invalid amount", validatorException.getMessage());
    }

    @Test
    void givenNegativeAmountInstallmentButMultiBeneficiaryThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAmount("0");
        installmentDTO.setFlagMultiBeneficiary(true);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        assertDoesNotThrow(() -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
    }

    @Test
    void givenInvalidAmountInstallmentThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAmount("xx");
        installmentDTO.setFlagMultiBeneficiary(true);
        DebtPositionTypeOrgDTO debtPositionTypeOrgDTO = new DebtPositionTypeOrgDTO();

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateAmountInstallment(installmentDTO, debtPositionTypeOrgDTO));
        assertEquals("Invalid amount", validatorException.getMessage());
    }

    @Test
    void givenNoPostalCodeThenReturnNullNation(){
        InstallmentDTO installmentDTO = new InstallmentDTO();

        NationDTO result = installmentsValidationActivity.validateNationAndPostalCode(installmentDTO);
        assertNull(result);
    }

    @Test
    void givenPostalCodeWithoutNationThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setPostalCode("00000");

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateNationAndPostalCode(installmentDTO));
        assertEquals("Nation is not valid", validatorException.getMessage());
    }

    @Test
    void givenPostalCodeWithoutNationIsoCodeThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setPostalCode("00000");
        installmentDTO.setNation(new NationDTO());

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateNationAndPostalCode(installmentDTO));
        assertEquals("Nation is not valid", validatorException.getMessage());
    }

    @Test
    void givenInvalidPostalCodeWithNationThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setPostalCode("POSTAL_CODE");
        installmentDTO.setNation(new NationDTO(1L, "Italia", "it"));

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateNationAndPostalCode(installmentDTO));
        assertEquals("Postal code is not valid", validatorException.getMessage());
    }

    @Test
    void givenValidNationWithWrongIsoCodeThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setPostalCode("00000");
        installmentDTO.setNation(new NationDTO(1L, "Italia", "xx"));

        when(positionDao.getNationByCodeIso("xx")).thenReturn(null);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateNationAndPostalCode(installmentDTO));
        assertEquals("Nation is not valid", validatorException.getMessage());
    }

    @Test
    void givenValidNationThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        NationDTO nationDTO = new NationDTO(1L, "Italia", "it");
        installmentDTO.setNation(nationDTO);

        when(positionDao.getNationByCodeIso("it")).thenReturn(nationDTO);

        NationDTO result = installmentsValidationActivity.validateNationAndPostalCode(installmentDTO);
        assertEquals(nationDTO, result);
    }

    @Test
    void givenValidProvinceThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        NationDTO nationDTO = new NationDTO(1L, "Italia", "it");
        ProvinceDTO provinceDTO = new ProvinceDTO(1L, "Milano", "MI");
        installmentDTO.setNation(nationDTO);
        installmentDTO.setProvince(provinceDTO);

        when(positionDao.getProvinceByAcronym("MI")).thenReturn(provinceDTO);

        ProvinceDTO result = installmentsValidationActivity.validateProvince(installmentDTO, nationDTO);
        assertEquals(provinceDTO, result);
    }

    @Test
    void givenInvalidProvinceThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        NationDTO nationDTO = new NationDTO(1L, "Italia", "it");
        ProvinceDTO provinceDTO = new ProvinceDTO(1L, "Milano", "XX");
        installmentDTO.setNation(nationDTO);
        installmentDTO.setProvince(provinceDTO);

        when(positionDao.getProvinceByAcronym("XX")).thenReturn(null);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateProvince(installmentDTO, nationDTO));
        assertEquals("Province is not valid", validatorException.getMessage());
    }

    @Test
    void givenValidProvinceWithInvalidNationThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        NationDTO nationDTO = new NationDTO(1L, "Italia", "XX");
        ProvinceDTO provinceDTO = new ProvinceDTO(1L, "Milano", "MI");
        installmentDTO.setNation(nationDTO);
        installmentDTO.setProvince(provinceDTO);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateProvince(installmentDTO, nationDTO));
        assertEquals("Province is not valid", validatorException.getMessage());
    }

    @Test
    void givenValidProvinceWithNullNationThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        ProvinceDTO provinceDTO = new ProvinceDTO(1L, "Milano", "MI");
        installmentDTO.setProvince(provinceDTO);

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateProvince(installmentDTO, null));
        assertEquals("Province is not valid", validatorException.getMessage());
    }

    @Test
    void givenNullProvinceThenReturnNull(){
        InstallmentDTO installmentDTO = new InstallmentDTO();

        ProvinceDTO result = installmentsValidationActivity.validateProvince(installmentDTO, null);
        assertNull(result);
    }

    @Test
    void givenMunicipalityWithoutProvinceThenThrowValidatorException(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setMunicipality(new CityDTO(1L, "Milano", 1L));

        ValidatorException validatorException = assertThrows(ValidatorException.class,
                () -> installmentsValidationActivity.validateMunicipality(installmentDTO, null));
        assertEquals("Location is not valid", validatorException.getMessage());
    }

    @Test
    void givenMunicipalityWithProvinceThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        CityDTO cityDTO = new CityDTO(1L, "Legnano", 1L);
        installmentDTO.setMunicipality(cityDTO);
        ProvinceDTO provinceDTO = new ProvinceDTO(1L, "Milano", "MI");

        when(positionDao.getMunicipalityByNameAndProvince("Legnano", "MI"))
                .thenReturn(Optional.of(cityDTO));

        String result = installmentsValidationActivity.validateMunicipality(installmentDTO, provinceDTO);
        assertEquals("Legnano", result);
    }

    @Test
    void givenMunicipalityWithWrongProvinceThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        CityDTO cityDTO = new CityDTO(1L, "Legnano", 1L);
        installmentDTO.setMunicipality(cityDTO);
        ProvinceDTO provinceDTO = new ProvinceDTO(2L, "Pavia", "PV");

        when(positionDao.getMunicipalityByNameAndProvince("Legnano", "PV"))
                .thenReturn(Optional.empty());

        String result = installmentsValidationActivity.validateMunicipality(installmentDTO, provinceDTO);
        assertEquals("Legnano", result);
    }

    @Test
    void givenNullMunicipalityNameThenReturnNull(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        CityDTO cityDTO = new CityDTO(1L, null, 1L);
        installmentDTO.setMunicipality(cityDTO);

        String result = installmentsValidationActivity.validateMunicipality(installmentDTO, null);
        assertNull(result);
    }

    @Test
    void givenValidAddressThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setAddress("via del test");

        assertDoesNotThrow(() -> installmentsValidationActivity.validateAddress(installmentDTO));
    }

    @Test
    void givenValidCivicThenSuccess(){
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setCivic("10");

        assertDoesNotThrow(() -> installmentsValidationActivity.validateAddress(installmentDTO));
    }
}
