package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import org.junit.jupiter.api.Test;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentIngestionFlowFileRequiredFieldsValidator.validateRequiredFields;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InstallmentIngestionFlowFileRequiredFieldsValidatorTest {


    @Test
    void givenObligatoryFieldsNullWhenValidateRequiredFieldsThenOk(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();

        validateRequiredFields(dto);

        assertEquals(true, dto.getFlagPagoPaPayment());
        assertEquals(false, dto.getFlagMultiBeneficiary());
        assertEquals(0, dto.getNumberBeneficiary());

        assertNotNull(dto.getDescription());
        assertEquals(1, dto.getPaymentOptionIndex());
        assertEquals("SINGLE_INSTALLMENT", dto.getPaymentOptionType());
        assertEquals("Pagamento Singolo Avviso", dto.getPaymentOptionDescription());
    }

    @Test
    void givenFlagMultiBeneficiaryTrueWhenValidateRequiredFieldsThenOk(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);

        validateRequiredFields(dto);

        assertEquals(1, dto.getNumberBeneficiary());
    }

    @Test
    void givenObligatoryFieldsNotNullWhenValidateRequiredFieldsThenDoNothing(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);
        dto.setFlagPagoPaPayment(Boolean.FALSE);
        dto.setFlagMultiBeneficiary(Boolean.TRUE);
        dto.setNumberBeneficiary(3);
        dto.setDescription("DP Description");
        dto.setPaymentOptionIndex(3);
        dto.setPaymentOptionType("Payment Option Type");
        dto.setPaymentOptionDescription("Payment option Description");

        validateRequiredFields(dto);

        assertEquals(false, dto.getFlagPagoPaPayment());
        assertEquals(true, dto.getFlagMultiBeneficiary());
        assertEquals(3, dto.getNumberBeneficiary());

        assertEquals("DP Description", dto.getDescription());
        assertEquals(3, dto.getPaymentOptionIndex());
        assertEquals("Payment Option Type", dto.getPaymentOptionType());
        assertEquals("Payment option Description", dto.getPaymentOptionDescription());
    }
}
