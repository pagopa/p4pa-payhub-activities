package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.payhub.activities.util.TreasuryUtils;
import it.gov.pagopa.pu.classification.dto.generated.Treasury;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TreasuryCsvMapperTest {
    @InjectMocks
    private TreasuryCsvMapper treasuryCsvMapperMock;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Test
    void givenTreasuryCsvIngestionFlowFileDTOWithCorrectFieldsWhenMapThenBuildTreasuryCorrectly() {
        TreasuryCsvIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
                TreasuryCsvIngestionFlowFileDTO.class);

        dto.setBillYear("2024");
        dto.setBillCode("112233");
        dto.setBillDate(LOCALDATE.toString());
        dto.setPspLastName("PSP_TEST");
        dto.setRemittanceDescription("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501");
        dto.setBillAmount("12.35");
        dto.setRegionValueDate(LOCALDATE.toString());

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        ingestionFlowFile.setIngestionFlowFileId(1L);
        Treasury result = treasuryCsvMapperMock.map(dto, ingestionFlowFile);

        Assertions.assertNotNull(result);

        assertEquals("2024", result.getBillYear());
        assertEquals("112233", result.getBillCode());
        assertEquals(LOCALDATE, result.getBillDate());
        assertEquals("PSP_TEST", result.getPspLastName());
        assertEquals("/PUR/LGPE-RIVERSAMENTO/URI/2025-01-15QWERTY-S2025011501", result.getRemittanceDescription());
        assertEquals(1235, result.getBillAmountCents());
        assertEquals(LOCALDATE, result.getRegionValueDate());
        assertEquals(TreasuryOrigin.TREASURY_CSV, result.getTreasuryOrigin());
        assertEquals(ORG_BT_CODE_DEFAULT, result.getOrgBtCode());
        assertEquals(ORG_ISTAT_CODE_DEFAULT, result.getOrgIstatCode());
        assertEquals(1, result.getIngestionFlowFileId());
        assertEquals(TreasuryUtils.getIdentificativo(dto.getRemittanceDescription(), TreasuryUtils.IUF), result.getIuf());
        assertEquals(123, result.getOrganizationId());
    }
}
