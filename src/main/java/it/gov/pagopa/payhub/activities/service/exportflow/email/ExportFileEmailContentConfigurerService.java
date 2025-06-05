package it.gov.pagopa.payhub.activities.service.exportflow.email;

import it.gov.pagopa.payhub.activities.config.EmailTemplatesConfiguration;
import it.gov.pagopa.pu.organization.dto.generated.Organization;
import it.gov.pagopa.pu.processexecutions.dto.generated.ExportFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Lazy
@Service
public class ExportFileEmailContentConfigurerService {

    private static final DateTimeFormatter MAILDATETIMEFORMATTER = DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");

    private final EmailTemplatesConfiguration emailTemplatesConfiguration;

    private static final Map<ExportFile.ExportFileTypeEnum, String> EXPORT_FILE_TYPE_MAP = Map.of(
            ExportFile.ExportFileTypeEnum.PAID, "pagati",
            ExportFile.ExportFileTypeEnum.RECEIPTS_ARCHIVING, "conservazione",
            ExportFile.ExportFileTypeEnum.CLASSIFICATIONS, "classificazione"
    );


    public ExportFileEmailContentConfigurerService(EmailTemplatesConfiguration emailTemplatesConfiguration) {
        this.emailTemplatesConfiguration = emailTemplatesConfiguration;
    }


    public Map<String, String> configureParams(ExportFile exportFile, Organization organization, boolean success) {
        return getMailParameters(exportFile.getFileName(), organization.getOrgName(),exportFile.getExportFileType(), success);
    }


    private Map<String, String> getMailParameters(String fileName, String orgName, ExportFile.ExportFileTypeEnum exportFileTypeEnum, boolean success) {
        Map<String, String> mailParams = new HashMap<>();
        String mailText;
        if (success) {
            mailText = emailTemplatesConfiguration.getExportMailTextOk();
            mailParams.put("fileName", fileName);

        } else {
            mailText = emailTemplatesConfiguration.getExportMailTextKo();
        }
        mailParams.put("exportFileType", getExportFileType(exportFileTypeEnum));
        mailParams.put("entityName", orgName);
        mailParams.put("mailText", mailText);
        mailParams.put("currentDate", MAILDATETIMEFORMATTER.format(LocalDateTime.now()));

        return mailParams;
    }


    private String getExportFileType(ExportFile.ExportFileTypeEnum exportFileTypeEnum) {
        return EXPORT_FILE_TYPE_MAP.getOrDefault(exportFileTypeEnum, null);
    }


}
