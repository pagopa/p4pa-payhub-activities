package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.xls;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.Xls.TreasuryXlsIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;

@Lazy
@Service
public class TreasuryXlsMapper {

    public Treasury map(TreasuryXlsIngestionFlowFileDTO dto, IngestionFlowFile ingestionFlowFile) {
        return Treasury.builder()
                .abiCode(dto.getAbiCode())
                .cabCode(dto.getCabCode())
                .accountCode(dto.getAccountCode())
                .billDate(dto.getBillDate())
                .billYear(extractYear(dto.getBillDate()))
                .regionValueDate(dto.getRegionValueDate())
                .billAmountCents(multiplyAmountBySing(dto))
                .remittanceCode(dto.getRemittanceCode())
                .checkNumber(dto.getCheckNumber())
                .bankReference(dto.getBankReference())
                .clientReference(dto.getClientReference())
                .remittanceDescription(dto.getRemittanceDescription())
                .iuf(TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF))
                .pspLastName(TreasuryUtils.getPspLastName(dto.getExtendedRemittanceDescription()))
                .billCode("XLS_" + TreasuryUtils.getIdentificativo(dto.getExtendedRemittanceDescription(), TreasuryUtils.IUF)) //TODO capire come andarlo a creare
                .orgIstatCode(ORG_ISTAT_CODE_DEFAULT)
                .orgBtCode(ORG_BT_CODE_DEFAULT)
                .ingestionFlowFileId(ingestionFlowFile.getIngestionFlowFileId())
                .organizationId(ingestionFlowFile.getOrganizationId())
                .treasuryOrigin(TreasuryOrigin.TREASURY_XLS)
                .build();
    }

    private static long multiplyAmountBySing(TreasuryXlsIngestionFlowFileDTO dto) {
        return dto.getBillAmountCents() * ("-".equals(dto.getSign()) ? -1 : 1);
    }

    private String extractYear(LocalDate localDate) {
        return String.valueOf(localDate.getYear());
    }
}
