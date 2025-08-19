package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csvcomplete;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csvcomplete.TreasuryCsvCompleteIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_BT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.service.ingestionflow.treasury.TreasuryVersionBaseHandlerService.ORG_ISTAT_CODE_DEFAULT;
import static it.gov.pagopa.payhub.activities.util.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TreasuryCsvCompleteMapperTest {

    @InjectMocks
    private TreasuryCsvCompleteMapper treasuryCsvCompleteMapper;

    private final PodamFactory podamFactory = TestUtils.getPodamFactory();

    @Test
    void map() {
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
                TreasuryCsvCompleteIngestionFlowFileDTO.class);

        dto.setBillYear("2025");
        dto.setBillDate(LOCALDATE.toString());
        dto.setOrgBtCode("BTCODE");
        dto.setOrgIstatCode("ISTATCODE");
        dto.setReceptionDate(null);
        dto.setActualSuspensionDate(null);
        dto.setRegionValueDate(null);

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        ingestionFlowFile.setIngestionFlowFileId(1L);
        var result = treasuryCsvCompleteMapper.map(dto, ingestionFlowFile);

        Assertions.assertNotNull(result);
        checkNotNullFields(result, "creationDate","updateDate","updateTraceId","treasuryId","updateOperatorExternalId", "links",
                "receptionDate", "actualSuspensionDate", "regionValueDate");
    }

    @Test
    void mapWithNullOrgValue() {
        TreasuryCsvCompleteIngestionFlowFileDTO dto = podamFactory.manufacturePojo(
                TreasuryCsvCompleteIngestionFlowFileDTO.class);

        dto.setBillYear("2025");
        dto.setBillDate(LOCALDATE.toString());
        dto.setOrgBtCode(null);
        dto.setOrgIstatCode(null);
        dto.setReceptionDate(OFFSETDATETIME.toString());
        dto.setActualSuspensionDate(LOCALDATE.toString());
        dto.setRegionValueDate(LOCALDATE.toString());

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        ingestionFlowFile.setIngestionFlowFileId(1L);
        var result = treasuryCsvCompleteMapper.map(dto, ingestionFlowFile);

        Assertions.assertNotNull(result);
        assertEquals(ORG_BT_CODE_DEFAULT, result.getOrgBtCode());
        assertEquals(ORG_ISTAT_CODE_DEFAULT, result.getOrgIstatCode());
        checkNotNullFields(result, "creationDate","updateDate","updateTraceId","treasuryId","updateOperatorExternalId", "links");
    }
}
