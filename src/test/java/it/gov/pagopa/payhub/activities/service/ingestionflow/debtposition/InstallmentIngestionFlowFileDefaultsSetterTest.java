package it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition;

import it.gov.pagopa.payhub.activities.dto.ingestion.debtposition.InstallmentIngestionFlowFileDTO;
import org.junit.jupiter.api.Test;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.debtposition.InstallmentIngestionFlowFileDefaultsSetter.setDefaultValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InstallmentIngestionFlowFileDefaultsSetterTest {

    @Test
    void givenObligatoryFieldsNullWhenSetDefaultValuesThenOk(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        setDefaultValues(dto);

        assertEquals(true, dto.getFlagPagoPaPayment());
        assertEquals(false, dto.getFlagMultiBeneficiary());
        assertEquals(0, dto.getNumberBeneficiary());

        assertNotNull(dto.getDescription());
        assertEquals(1, dto.getPaymentOptionIndex());
        assertEquals("SINGLE_INSTALLMENT", dto.getPaymentOptionType());
        assertEquals("Pagamento Singolo Avviso", dto.getPaymentOptionDescription());
    }

    @Test
    void givenFlagMultiBeneficiaryTrueWhenSetDefaultValuesThenOk(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);

        setDefaultValues(dto);

        assertEquals(1, dto.getNumberBeneficiary());
    }

    @Test
    void givenObligatoryFieldsNotNullWhenSetDefaultValuesThenDoNothing(){
        InstallmentIngestionFlowFileDTO dto = new InstallmentIngestionFlowFileDTO();
        dto.setFlagMultiBeneficiary(true);
        dto.setFlagPagoPaPayment(Boolean.FALSE);
        dto.setFlagMultiBeneficiary(Boolean.TRUE);
        dto.setNumberBeneficiary(3);
        dto.setDescription("DP Description");
        dto.setPaymentOptionIndex(3);
        dto.setPaymentOptionType("Payment Option Type");
        dto.setPaymentOptionDescription("Payment option Description");

        setDefaultValues(dto);

        assertEquals(false, dto.getFlagPagoPaPayment());
        assertEquals(true, dto.getFlagMultiBeneficiary());
        assertEquals(3, dto.getNumberBeneficiary());

        assertEquals("DP Description", dto.getDescription());
        assertEquals(3, dto.getPaymentOptionIndex());
        assertEquals("Payment Option Type", dto.getPaymentOptionType());
        assertEquals("Payment option Description", dto.getPaymentOptionDescription());
    }
}
