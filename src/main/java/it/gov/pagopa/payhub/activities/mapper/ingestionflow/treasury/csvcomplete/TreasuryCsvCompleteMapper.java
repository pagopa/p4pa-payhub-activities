package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class TreasuryCsvCompleteMapper {

    public Treasury map(TreasuryCsvCompleteIngestionFlowFileDTO dto, IngestionFlowFile ingestionFlowFile) {

        LocalDate billDate = LocalDate.parse(dto.getBillDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        OffsetDateTime offsetDateTime = null;
        if (dto.getReceptionDate() != null) {
            offsetDateTime = OffsetDateTime.parse(dto.getReceptionDate(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        LocalDate regionValueDate = null;
        if (dto.getRegionValueDate() != null) {
            regionValueDate = LocalDate.parse(dto.getRegionValueDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

        boolean regularized = dto.getIsRegularized() != null && "Y".equalsIgnoreCase(dto.getIsRegularized());

        LocalDate actualSuspensionDate = null;
        if (dto.getActualSuspensionDate() != null) {
            actualSuspensionDate = LocalDate.parse(dto.getActualSuspensionDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return Treasury.builder()
                .billYear(dto.getBillYear())
                .billCode(dto.getBillCode())
                .ingestionFlowFileId(dto.getIngestionFlowFileId())
                .organizationId(ingestionFlowFile.getOrganizationId())
                .iuf(dto.getIuf())
                .iuv(dto.getIuv())
                .accountCode(dto.getAccountCode())
                .domainIdCode(dto.getDomainIdCode())
                .transactionTypeCode(dto.getTransactionTypeCode())
                .remittanceCode(dto.getRemittanceCode())
                .remittanceDescription(dto.getRemittanceDescription())
                .billAmountCents(dto.getBillAmountCents())
                .billDate(billDate)
                .receptionDate(offsetDateTime)
                .documentYear(dto.getDocumentYear())
                .documentCode(dto.getDocumentCode())
                .sealCode(dto.getSealCode())
                .pspLastName(dto.getPspLastName())
                .pspFirstName(dto.getPspFirstName())
                .pspAddress(dto.getPspAddress())
                .pspPostalCode(dto.getPspPostalCode())
                .pspCity(dto.getPspCity())
                .pspFiscalCode(dto.getPspFiscalCode())
                .pspVatNumber(dto.getPspVatNumber())
                .abiCode(dto.getAbiCode())
                .cabCode(dto.getCabCode())
                .ibanCode(dto.getIbanCode())
                .accountRegistryCode(dto.getAccountRegistryCode())
                .provisionalAe(dto.getProvisionalAe())
                .provisionalCode(dto.getProvisionalCode())
                .accountTypeCode(dto.getAccountTypeCode())
                .processCode(dto.getProcessCode())
                .executionPgCode(dto.getExecutionPgCode())
                .transferPgCode(dto.getTransferPgCode())
                .processPgNumber(dto.getProcessPgNumber())
                .regionValueDate(regionValueDate)
                .regularized(regularized)
                .actualSuspensionDate(actualSuspensionDate)
                .managementProvisionalCode(dto.getManagementProvisionalCode())
                .endToEndId(dto.getEndToEndCode())
                .build();
    }

}

