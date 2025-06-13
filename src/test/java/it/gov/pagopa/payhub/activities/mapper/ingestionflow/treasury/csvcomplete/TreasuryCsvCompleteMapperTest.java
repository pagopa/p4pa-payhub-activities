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

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;

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
}
