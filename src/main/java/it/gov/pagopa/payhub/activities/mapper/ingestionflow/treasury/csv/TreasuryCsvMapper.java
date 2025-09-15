package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;

@Service
public class TreasuryCsvMapper {

    public Treasury map(TreasuryCsvIngestionFlowFileDTO dto, IngestionFlowFile ingestionFlowFile) {
        LocalDate billDate = LocalDate.parse(dto.getBillDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        LocalDate regionValueDate = null;
        if (dto.getRegionValueDate() != null) {
            regionValueDate = LocalDate.parse(dto.getRegionValueDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

        return Treasury.builder()
                .billAmountCents(dto.getBillAmountCents())
                .billYear(dto.getBillYear())
                .billCode(dto.getBillCode())
                .billDate(billDate)
                .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
                .iuf(TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF))
                .organizationId(ingestionFlowFile.getOrganizationId())
                .orgBtCode(ORG_BT_CODE_DEFAULT)
                .orgIstatCode(ORG_ISTAT_CODE_DEFAULT)
                .pspLastName(dto.getPspLastName())
                .regionValueDate(regionValueDate)
                .remittanceDescription(dto.getRemittanceDescription())
                .treasuryOrigin(TreasuryOrigin.TREASURY_CSV)
                .build();
    }
}
