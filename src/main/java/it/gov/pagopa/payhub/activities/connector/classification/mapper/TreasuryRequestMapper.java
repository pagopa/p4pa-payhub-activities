package it.gov.pagopa.payhub.activities.connector.classification.mapper;

import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryRequestBody;

public class TreasuryRequestMapper {

    private TreasuryRequestMapper() {
    }

    public static TreasuryRequestBody map(Treasury treasury) {
        if (treasury == null) {
            return null;
        }

        return TreasuryRequestBody.builder()
                .treasuryId(treasury.getTreasuryId())
                .billYear(treasury.getBillYear())
                .billCode(treasury.getBillCode())
                .accountCode(treasury.getAccountCode())
                .domainIdCode(treasury.getDomainIdCode())
                .transactionTypeCode(treasury.getTransactionTypeCode())
                .remittanceCode(treasury.getRemittanceCode())
                .remittanceInformation(treasury.getRemittanceInformation())
                .billAmountCents(treasury.getBillAmountCents())
                .billDate(treasury.getBillDate())
                .receptionDate(treasury.getReceptionDate())
                .documentYear(treasury.getDocumentYear())
                .documentCode(treasury.getDocumentCode())
                .sealCode(treasury.getSealCode())
                .pspLastName(treasury.getPspLastName())
                .pspFirstName(treasury.getPspFirstName())
                .pspAddress(treasury.getPspAddress())
                .pspPostalCode(treasury.getPspPostalCode())
                .pspCity(treasury.getPspCity())
                .pspFiscalCode(treasury.getPspFiscalCode())
                .pspVatNumber(treasury.getPspVatNumber())
                .abiCode(treasury.getAbiCode())
                .cabCode(treasury.getCabCode())
                .accountRegistryCode(treasury.getAccountRegistryCode())
                .provisionalAe(treasury.getProvisionalAe())
                .provisionalCode(treasury.getProvisionalCode())
                .ibanCode(treasury.getIbanCode())
                .accountTypeCode(treasury.getAccountTypeCode())
                .processCode(treasury.getProcessCode())
                .executionPgCode(treasury.getExecutionPgCode())
                .transferPgCode(treasury.getTransferPgCode())
                .processPgNumber(treasury.getProcessPgNumber())
                .regionValueDate(treasury.getRegionValueDate())
                .organizationId(treasury.getOrganizationId())
                .iuf(treasury.getIuf())
                .iuv(treasury.getIuv())
                .creationDate(treasury.getCreationDate())
                .updateDate(treasury.getUpdateDate())
                .ingestionFlowFileId(treasury.getIngestionFlowFileId())
                .actualSuspensionDate(treasury.getActualSuspensionDate())
                .managementProvisionalCode(treasury.getManagementProvisionalCode())
                .endToEndId(treasury.getEndToEndId())
                .regularized(treasury.getRegularized())
                .updateOperatorExternalId(treasury.getUpdateOperatorExternalId())
                .build();
    }


}
