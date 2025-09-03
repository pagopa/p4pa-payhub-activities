package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ReceiptIngestionFlowFileRequiredFieldsValidatorService {
    private final OrganizationService organizationService;

    public ReceiptIngestionFlowFileRequiredFieldsValidatorService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public static void setDefaultValues(ReceiptIngestionFlowFileDTO dto) {
        if (StringUtils.isBlank(dto.getRemittanceInformation())) {
            dto.setRemittanceInformation("Causale Default iuv: " + dto.getCreditorReferenceId());
        }

        if (StringUtils.isBlank(dto.getFiscalCodePA())) {
            dto.setFiscalCodePA(dto.getOrgFiscalCode());
        }

        if (dto.getIdTransfer() == null) {
            dto.setIdTransfer(1);
        }

        if (dto.getSinglePaymentAmount() == null) {
            dto.setSinglePaymentAmount(dto.getPaymentAmountCents());
        }

        if (StringUtils.isBlank(dto.getTransferCategory())) {
            dto.setTransferCategory("UNKNOWN");
        }
    }

    public boolean isValidOrganization(IngestionFlowFile ingestionFlowFile, ReceiptIngestionFlowFileDTO receiptIngestionFlowFileDTO) {
        Long organizationId = ingestionFlowFile.getOrganizationId();
        Organization org = organizationService.getOrganizationById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization with id " + organizationId + " not found."));

        String orgFiscalCode = org.getOrgFiscalCode();
        String receiptOrgFiscalCode = receiptIngestionFlowFileDTO.getOrgFiscalCode();
        String receiptFiscalCodePA = receiptIngestionFlowFileDTO.getFiscalCodePA();

        if (StringUtils.isBlank(receiptFiscalCodePA)) {
            return orgFiscalCode.equals(receiptOrgFiscalCode);
        }

        return orgFiscalCode.equals(receiptOrgFiscalCode) && orgFiscalCode.equals(receiptFiscalCodePA);
    }

}
