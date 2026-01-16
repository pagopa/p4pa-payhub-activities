package it.gov.pagopa.payhub.activities.service.ingestionflow.receipt;

import it.gov.pagopa.payhub.activities.connector.debtposition.ReceiptService;
import it.gov.pagopa.payhub.activities.connector.organization.OrganizationService;
import it.gov.pagopa.payhub.activities.dto.ingestion.receipt.ReceiptIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.enums.FileErrorCode;
import it.gov.pagopa.payhub.activities.exception.organization.OrganizationNotFoundException;
import it.gov.pagopa.pu.debtposition.dto.generated.ReceiptNoPII;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class ReceiptIngestionFlowFileRequiredFieldsValidatorService {
    private final OrganizationService organizationService;
    private final ReceiptService receiptService;

    public ReceiptIngestionFlowFileRequiredFieldsValidatorService(OrganizationService organizationService, ReceiptService receiptService) {
        this.organizationService = organizationService;
        this.receiptService = receiptService;
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

    public void validateIngestionFile(IngestionFlowFile ingestionFlowFile, ReceiptIngestionFlowFileDTO dto) {
        validateOrganization(ingestionFlowFile, dto);

        if (!dto.getIuv().equals(dto.getCreditorReferenceId())) {
            throw new IllegalArgumentException(
                    String.format("[%s] codIuv and identificativoUnivocoVersamento must be equal, but found iuv='%s' and creditorReferenceId='%s'",
                            FileErrorCode.RECEIPT_IUV_MISMATCH.name(), dto.getIuv(), dto.getCreditorReferenceId()));
        }

        ReceiptNoPII receipt = receiptService.getByPaymentReceiptId(dto.getPaymentReceiptId());
        if (receipt != null && !receipt.getCreditorReferenceId().equals(dto.getCreditorReferenceId())) {
            throw new IllegalArgumentException(
                    String.format("[%s] A receipt with paymentReceiptId='%s' already exists and is associated with a different installment (existing IUV='%s', provided IUV='%s')",
                            FileErrorCode.RECEIPT_ALREADY_ASSOCIATED_TO_ANOTHER_IUV.name(), dto.getPaymentReceiptId(), receipt.getCreditorReferenceId(), dto.getCreditorReferenceId()
                    )
            );
        }
    }

    private void validateOrganization(IngestionFlowFile ingestionFlowFile, ReceiptIngestionFlowFileDTO receiptIngestionFlowFileDTO) {
        Long organizationId = ingestionFlowFile.getOrganizationId();
        Organization org = organizationService.getOrganizationById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException(String.format("[%s] Organization with id %s not found",
                        FileErrorCode.ORGANIZATION_NOT_FOUND.name(), organizationId)));

        String orgFiscalCode = org.getOrgFiscalCode();
        String receiptOrgFiscalCode = receiptIngestionFlowFileDTO.getOrgFiscalCode();
        String receiptFiscalCodePA = receiptIngestionFlowFileDTO.getFiscalCodePA();

        boolean isValid;
        if (StringUtils.isBlank(receiptFiscalCodePA)) {
            isValid = orgFiscalCode.equals(receiptOrgFiscalCode);
        } else {
            isValid = orgFiscalCode.equals(receiptOrgFiscalCode) && orgFiscalCode.equals(receiptFiscalCodePA);
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    FileErrorCode.RECEIPT_ORG_MISMATCH.name() + " Organization fiscal codes must all be equal (organization, receipt.orgFiscalCode, receipt.fiscalCodePA)."
            );
        }

        if(StringUtils.isBlank(receiptIngestionFlowFileDTO.getCompanyName())){
            receiptIngestionFlowFileDTO.setCompanyName(org.getOrgName());
        }
    }

}
