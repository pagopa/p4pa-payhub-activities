package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptIngestionFlowFileRequiredFieldsValidator.setDefaultValues;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReceiptIngestionFlowFileRequiredFieldsValidatorTest {

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Test
    void givenObligatoryFieldsNullWhenValidateRequiredFieldsThenOk(){
        ReceiptIngestionFlowFileDTO dto =  podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setRemittanceInformation(null);
        dto.setFiscalCodePA(null);

        setDefaultValues(dto);

        assertEquals("Causale Default iuv: " + dto.getCreditorReferenceId(), dto.getRemittanceInformation());
        assertEquals(dto.getOrgFiscalCode(), dto.getFiscalCodePA());
    }

    @Test
    void givenObligatoryFieldsNotNullWhenValidateRequiredFieldsThenDoNothing(){
        ReceiptIngestionFlowFileDTO dto =  podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);

        setDefaultValues(dto);

        assertNotNull(dto.getRemittanceInformation());
        assertNotNull(dto.getFiscalCodePA());
    }
}
