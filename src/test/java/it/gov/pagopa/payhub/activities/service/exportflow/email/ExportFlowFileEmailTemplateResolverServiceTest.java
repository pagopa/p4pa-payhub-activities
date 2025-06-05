package it.gov.pagopa.payhub.activities.service.exportflow.email;

import it.gov.pagopa.payhub.activities.enums.EmailTemplateName;
import it.gov.pagopa.payhub.activities.exception.exportflow.ExportFileTypeNotSupported;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Set;

class ExportFlowFileEmailTemplateResolverServiceTest {

    private final ExportFlowFileEmailTemplateResolverService service = new ExportFlowFileEmailTemplateResolverService();

    private final Set<ExportFile.ExportFileTypeEnum> expectedUnsupported = Set.of(
            ExportFile.ExportFileTypeEnum.PAYMENTS_REPORTING
    );

    @ParameterizedTest
    @EnumSource(ExportFile.ExportFileTypeEnum.class)
    void whenResolveThenReturnExpected(ExportFile.ExportFileTypeEnum exportFileTypeEnum){
        // Given
        ExportFile exportFile = new ExportFile();
        exportFile.setExportFileType(exportFileTypeEnum);

        if(expectedUnsupported.contains(exportFileTypeEnum)){
            Assertions.assertThrows(ExportFileTypeNotSupported.class, () -> service.resolve(exportFile, true));
        } else {
            // When success=true
            EmailTemplateName result = service.resolve(exportFile, true);
            // Then
            Assertions.assertEquals("EXPORT_" + exportFileTypeEnum + "_OK", result.toString());

            // When success=false
            result = service.resolve(exportFile, false);
            // Then
            Assertions.assertEquals("EXPORT_" + exportFileTypeEnum + "_KO", result.toString());
        }
    }
}