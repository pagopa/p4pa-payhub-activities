package it.gov.pagopa.payhub.activities.service.ingestionflow.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.ingestionflow.IngestionFlowTypeNotSupportedException;
import it.gov.pagopa.pu.processexecutions.dto.generated.IngestionFlowFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

class IngestionFlowFileEmailTemplateResolverServiceTest {

    private final Set<IngestionFlowFile.IngestionFlowFileTypeEnum> expectedUnsupported = Set.of(
            IngestionFlowFile.IngestionFlowFileTypeEnum.RECEIPT_PAGOPA
    );

    private final IngestionFlowFileEmailTemplateResolverService service = new IngestionFlowFileEmailTemplateResolverService();

    @ParameterizedTest
    @EnumSource(IngestionFlowFile.IngestionFlowFileTypeEnum.class)
    void whenResolveThenReturnExpected(IngestionFlowFile.IngestionFlowFileTypeEnum ingestionFlowFileType) {
        // Given
        IngestionFlowFile ingestionFlowFile = new IngestionFlowFile();
        ingestionFlowFile.setIngestionFlowFileType(ingestionFlowFileType);

        if (expectedUnsupported.contains(ingestionFlowFileType)) {
            Assertions.assertThrows(IngestionFlowTypeNotSupportedException.class, () -> service.resolve(ingestionFlowFile, true));
        } else {
            // When success=true
            EmailTemplateName result = service.resolve(ingestionFlowFile, true);
            // Then
            Assertions.assertEquals("INGESTION_" + ingestionFlowFileType + "_OK", result.toString());

            // When success=false
            result = service.resolve(ingestionFlowFile, false);
            // Then
            Assertions.assertEquals("INGESTION_" + ingestionFlowFileType + "_KO", result.toString());
        }
    }
}
