package it.gov.pagopa.payhub.activities.service.exportflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class ExportFileEmailContentConfigurerServiceTest {

    private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");

    @Mock
    private EmailTemplatesConfiguration emailTemplatesConfigurationMock;

    private ExportFileEmailContentConfigurerService exportFileEmailContentConfigurerService;

    @BeforeEach
    void setUp() {
        exportFileEmailContentConfigurerService = new ExportFileEmailContentConfigurerService(emailTemplatesConfigurationMock);
    }

    @Test
    void givenSuccessTrueWhenConfigureParamsThenReturnMailParams() {
        //given
        ExportFile exportFile = new ExportFile();
        exportFile.setFileName("exportFileName");
        exportFile.setExportFileType(ExportFile.ExportFileTypeEnum.PAID);

        Organization organization = new Organization();
        organization.setOrgName("orgName");

        Mockito.when(emailTemplatesConfigurationMock.getExportMailTextOk()).thenReturn("EXPORT_OK");
        //when
        Map<String, String> result = exportFileEmailContentConfigurerService.configureParams(exportFile, organization, true);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertEquals("exportFileName",result.get("fileName"));
        Assertions.assertEquals("orgName",result.get("entityName"));
        Assertions.assertEquals("EXPORT_OK", result.get("mailText"));
        Assertions.assertEquals("pagati", result.get("exportFileType"));
        Assertions.assertEquals(MAILDATETIMEFORMATTER.format(LocalDateTime.now()), result.get("currentDate"));
    }

    @Test
    void givenSuccessFalseWhenConfigureParamsThenReturnMailParams() {
        //given
        ExportFile exportFile = new ExportFile();
        exportFile.setExportFileType(ExportFile.ExportFileTypeEnum.CLASSIFICATIONS);
        Organization organization = new Organization();
        organization.setOrgName("orgName");

        Mockito.when(emailTemplatesConfigurationMock.getExportMailTextKo()).thenReturn("EXPORT_KO");
        //when
        Map<String, String> result = exportFileEmailContentConfigurerService.configureParams(exportFile, organization, false);
        //then
        Assertions.assertNotNull(result);
        Assertions.assertNull(result.get("exportUrl"));
        Assertions.assertNull(result.get("fileName"));
        Assertions.assertEquals("orgName",result.get("entityName"));
        Assertions.assertEquals("EXPORT_KO", result.get("mailText"));
        Assertions.assertEquals("classificazione", result.get("exportFileType"));
        Assertions.assertEquals(MAILDATETIMEFORMATTER.format(LocalDateTime.now()), result.get("currentDate"));
    }

    @ParameterizedTest
    @MethodSource("provideExportFileTypeMappings")
    void givenExportFileTypeEnumWhenConfigureParamsThenReturnExpectedString(
            ExportFile.ExportFileTypeEnum exportFileTypeEnum, String expectedExportFileType) {

        ExportFile exportFile = new ExportFile();
        exportFile.setExportFileType(exportFileTypeEnum);
        Organization organization = new Organization();

        Map<String, String> result = exportFileEmailContentConfigurerService.configureParams(exportFile, organization, true);

        Assertions.assertEquals(expectedExportFileType, result.get("exportFileType"));
    }

    private static Stream<Arguments> provideExportFileTypeMappings() {
        return Stream.of(
                Arguments.of(ExportFile.ExportFileTypeEnum.PAID, "pagati"),
                Arguments.of(ExportFile.ExportFileTypeEnum.RECEIPTS_ARCHIVING, "conservazione"),
                Arguments.of(ExportFile.ExportFileTypeEnum.CLASSIFICATIONS, "classificazione")
        );
    }

}