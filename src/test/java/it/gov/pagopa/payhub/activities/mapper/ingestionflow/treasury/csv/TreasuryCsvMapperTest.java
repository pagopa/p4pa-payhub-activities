package it.gov.pagopa.payhub.activities.mapper.ingestionflow.treasury.csv;

import it.gov.pagopa.payhub.activities.dto.ingestion.treasury.csv.TreasuryCsvIngestionFlowFileDTO;
import it.gov.pagopa.payhub.activities.util.TestUtils;
import it.gov.pagopa.pu.classification.dto.generated.TreasuryOrigin;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;

import static it.gov.pagopa.payhub.activities.util.TestUtils.LOCALDATE;
import static it.gov.pagopa.payhub.activities.util.TestUtils.checkNotNullFields;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TreasuryCsvMapperTest {
    @InjectMocks
    private TreasuryCsvMapper treasuryCsvMapper;

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
        dto.setBillAmountCents(1235L);
        dto.setRegionValueDate(LOCALDATE.toString());

        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setOrganizationId(123L);
        ingestionFlowFile.setIngestionFlowFileId(1L);
        var result = treasuryCsvMapper.map(dto, ingestionFlowFile);

        Assertions.assertNotNull(result);
        assertEquals(TreasuryOrigin.TREASURY_CSV, result.getTreasuryOrigin());

        checkNotNullFields(result, "creationDate", "updateDate", "updateTraceId", "treasuryId", "updateOperatorExternalId", "links",
                "receptionDate", "actualSuspensionDate", "regionValueDate", "checkNumber", "clientReference", "bankReference", "iuv", "accountCode",
                "domainIdCode", "transactionTypeCode", "remittanceCode", "documentYear", "documentCode", "sealCode", "pspFirstName", "pspAddress",
                "pspPostalCode", "pspCity", "pspFiscalCode", "pspVatNumber", "abiCode", "cabCode", "ibanCode", "accountRegistryCode", "provisionalAe",
                "provisionalCode", "accountTypeCode", "processCode", "executionPgCode", "transferPgCode", "processPgNumber", "managementProvisionalCode",
                "endToEndId", "regularized");
    }
}
