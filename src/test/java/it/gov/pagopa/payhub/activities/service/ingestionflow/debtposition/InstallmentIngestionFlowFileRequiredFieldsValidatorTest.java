package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.InvalidIngestionFileException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentIngestionFlowFileRequiredFieldsValidator.validateRequiredFields;
import static org.junit.jupiter.api.Assertions.*;

class InstallmentIngestionFlowFileRequiredFieldsValidatorTest {

    @Test
    void givenRequiredFieldsNullWhenValidateRequiredFieldsThenThrowIllegalStateException(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();

        InvalidIngestionFileException result =
                assertThrows(InvalidIngestionFileException.class, () -> validateRequiredFields(dto));

        assertTrue(result.getMessage().contains("EntityType"));
        assertTrue(result.getMessage().contains("FiscalCode"));
        assertTrue(result.getMessage().contains("FullName"));
        assertTrue(result.getMessage().contains("Amount"));
        assertTrue(result.getMessage().contains("DebtPositionTypeCode"));
        assertTrue(result.getMessage().contains("RemittanceInformation"));
        assertTrue(result.getMessage().contains("Action"));
    }

    @Test
    void givenObligatoryFieldsNullWhenValidateRequiredFieldsThenOk(){
        InstallmentIngestionFlowFileDTO dto = buildInstallmentIngestionFlowFileDTO();

        validateRequiredFields(dto);

        assertEquals(true, dto.getFlagPuPagoPaPayment());
        assertEquals(false, dto.getFlagMultiBeneficiary());
        assertEquals(0, dto.getNumberBeneficiary());

        assertNotNull(dto.getDescription());
        assertEquals(1, dto.getPaymentOptionIndex());
        assertEquals("SINGLE_INSTALLMENT", dto.getPaymentOptionType());
        assertEquals("Pagamento Singolo Avviso", dto.getPaymentOptionDescription());
    }

    @Test
    void givenFlagMultiBeneficiaryTrueWhenValidateRequiredFieldsThenOk(){
        InstallmentIngestionFlowFileDTO dto = buildInstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);

        validateRequiredFields(dto);

        assertEquals(1, dto.getNumberBeneficiary());
    }

    @Test
    void givenObligatoryFieldsNotNullWhenValidateRequiredFieldsThenDoNothing(){
        InstallmentIngestionFlowFileDTO dto = buildInstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);
        dto.setFlagPuPagoPaPayment(Boolean.FALSE);
        dto.setFlagMultiBeneficiary(Boolean.TRUE);
        dto.setNumberBeneficiary(3);
        dto.setDescription("DP Description");
        dto.setPaymentOptionIndex(3);
        dto.setPaymentOptionType("Payment Option Type");
        dto.setPaymentOptionDescription("Payment option Description");

        validateRequiredFields(dto);

        assertEquals(false, dto.getFlagPuPagoPaPayment());
        assertEquals(true, dto.getFlagMultiBeneficiary());
        assertEquals(3, dto.getNumberBeneficiary());

        assertEquals("DP Description", dto.getDescription());
        assertEquals(3, dto.getPaymentOptionIndex());
        assertEquals("Payment Option Type", dto.getPaymentOptionType());
        assertEquals("Payment option Description", dto.getPaymentOptionDescription());
    }

    private static InstallmentIngestionFlowFileDTO buildInstallmentIngestionFlowFileDTO() {
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        dto.setEntityType(InstallmentIngestionFlowFileDTO.EntityTypeEnum.F);
        dto.setFiscalCode("FiscalCode");
        dto.setFullName("FullName");
        dto.setAmount(BigDecimal.TEN);
        dto.setDebtPositionTypeCode("DebtPositionTypeCode");
        dto.setRemittanceInformation("RemittanceInformation");
        dto.setAction(InstallmentIngestionFlowFileDTO.ActionEnum.I);
        return dto;
    }
}
