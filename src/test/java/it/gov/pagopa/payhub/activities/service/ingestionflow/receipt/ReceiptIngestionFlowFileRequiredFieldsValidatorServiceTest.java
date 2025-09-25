package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import java.util.Optional;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptIngestionFlowFileRequiredFieldsValidatorService.setDefaultValues;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.receipt.ReceiptIngestionFlowFileRequiredFieldsValidatorService.validateIuvMatchesCreditorReferenceId;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReceiptIngestionFlowFileRequiredFieldsValidatorServiceTest {
    @Mock
    private OrganizationService organizationServiceMock;

    private ReceiptIngestionFlowFileRequiredFieldsValidatorService receiptIngestionFlowFileRequiredFieldsValidatorService;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @BeforeEach
    void setUp() {
        receiptIngestionFlowFileRequiredFieldsValidatorService = new ReceiptIngestionFlowFileRequiredFieldsValidatorService(organizationServiceMock);
    }

    @Test
    void givenObligatoryFieldsNullWhenValidateRequiredFieldsThenOk(){
        ReceiptIngestionFlowFileDTO dto =  podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);
        dto.setRemittanceInformation(null);
        dto.setFiscalCodePA(null);
        dto.setIdTransfer(null);
        dto.setSinglePaymentAmount(null);
        dto.setTransferCategory(null);

        setDefaultValues(dto);

        assertEquals("Causale Default iuv: " + dto.getCreditorReferenceId(), dto.getRemittanceInformation());
        assertEquals(dto.getOrgFiscalCode(), dto.getFiscalCodePA());
        assertEquals(1, dto.getIdTransfer());
        assertEquals(dto.getSinglePaymentAmount(), dto.getPaymentAmountCents());
        assertEquals("UNKNOWN", dto.getTransferCategory());
    }

    @Test
    void givenObligatoryFieldsNotNullWhenValidateRequiredFieldsThenDoNothing(){
        ReceiptIngestionFlowFileDTO dto =  podamFactory.manufacturePojo(ReceiptIngestionFlowFileDTO.class);

        setDefaultValues(dto);

        assertNotNull(dto.getRemittanceInformation());
        assertNotNull(dto.getFiscalCodePA());
        assertNotNull(dto.getIdTransfer());
        assertNotNull(dto.getSinglePaymentAmount());
        assertNotNull(dto.getTransferCategory());
    }

    @Test
    void givenFiscalCodePAIsNullWhenIsValidOrganizationThenReturnTrueIfOrgFiscalMatches() {
        IngestionFlowFile flowFile = new IngestionFlowFile();
        flowFile.setOrganizationId(1L);

        Organization org = new Organization();
        org.setOrgFiscalCode("ORG123");

        ReceiptIngestionFlowFileDTO dto = new ReceiptIngestionFlowFileDTO();
        dto.setOrgFiscalCode("ORG123");
        dto.setFiscalCodePA(null);

        Mockito.when(organizationServiceMock.getOrganizationById(1L)).thenReturn(Optional.of(org));

        assertTrue(receiptIngestionFlowFileRequiredFieldsValidatorService.isValidOrganization(flowFile, dto));
    }

    @Test
    void givenFiscalCodePAIsBlankWhenIsValidOrganizationThenReturnTrueIfOrgFiscalMatches() {
        IngestionFlowFile flowFile = new IngestionFlowFile();
        flowFile.setOrganizationId(2L);

        Organization org = new Organization();
        org.setOrgFiscalCode("ORG456");

        ReceiptIngestionFlowFileDTO dto = new ReceiptIngestionFlowFileDTO();
        dto.setOrgFiscalCode("ORG456");
        dto.setFiscalCodePA("   ");

        Mockito.when(organizationServiceMock.getOrganizationById(2L)).thenReturn(Optional.of(org));

        assertTrue(receiptIngestionFlowFileRequiredFieldsValidatorService.isValidOrganization(flowFile, dto));
    }

    @Test
    void givenFiscalCodePAIsFilledWhenIsValidOrganizationThenReturnTrueOnlyIfBothMatch() {
        IngestionFlowFile flowFile = new IngestionFlowFile();
        flowFile.setOrganizationId(3L);

        Organization org = new Organization();
        org.setOrgFiscalCode("ORG789");

        ReceiptIngestionFlowFileDTO dto = new ReceiptIngestionFlowFileDTO();
        dto.setOrgFiscalCode("ORG789");
        dto.setFiscalCodePA("ORG789");

        Mockito.when(organizationServiceMock.getOrganizationById(3L)).thenReturn(Optional.of(org));

        assertTrue(receiptIngestionFlowFileRequiredFieldsValidatorService.isValidOrganization(flowFile, dto));

        dto.setFiscalCodePA("OTHER");
        assertFalse(receiptIngestionFlowFileRequiredFieldsValidatorService.isValidOrganization(flowFile, dto));
    }

    @Test
    void givenOrgMatchesButFiscalCodePADifferentWhenIsValidOrganizationThenReturnFalse() {
        IngestionFlowFile flowFile = new IngestionFlowFile();
        flowFile.setOrganizationId(4L);

        Organization org = new Organization();
        org.setOrgFiscalCode("ORG123");

        ReceiptIngestionFlowFileDTO dto = new ReceiptIngestionFlowFileDTO();
        dto.setOrgFiscalCode("ORG1234");
        dto.setFiscalCodePA("DIFFERENT");

        Mockito.when(organizationServiceMock.getOrganizationById(4L)).thenReturn(Optional.of(org));

        assertFalse(receiptIngestionFlowFileRequiredFieldsValidatorService.isValidOrganization(flowFile, dto));
    }

    @Test
    void givenDifferentIuvAndCreditorReferenceIdWhenValidateIuvMatchesCreditorReferenceIdThenThrowException() {
        ReceiptIngestionFlowFileDTO dto = new ReceiptIngestionFlowFileDTO();
        dto.setIuv("IUV");
        dto.setCreditorReferenceId("DIFFERENT");

        assertThrows(IllegalArgumentException.class, () ->validateIuvMatchesCreditorReferenceId(dto));
    }

}
