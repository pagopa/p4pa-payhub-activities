package helper;

import it.gov.pagopa.payhub.activities.dto.MailDTO;
import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import it.gov.pagopa.payhub.activities.helper.MailParameterHelper;
import it.gov.pagopa.payhub.activities.utils.Constants;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class IngestionMailHelper {
    /**
     *
     * @param ingestionFlowDTO dto containing ingestion flow table data
     * @param actualDate  formatted actual date
     * @return MailDTO dto containing mail data
     */
    public static MailDTO getMailIngestionFlowText(IngestionFlowDTO ingestionFlowDTO, String actualDate) throws Exception {
        Long fileSize = ingestionFlowDTO.getDownloadedFileSize();
        Long totalRowsNumber = ingestionFlowDTO.getTotalRowsNumber();

        Properties properties = MailParameterHelper.getProperties();
        String templateOK = properties.getProperty("template.mail-importFlussoRendicontazione.loadFileOK");
        String templateKO = properties.getProperty("template.mail-importFlussoRendicontazione.loadFileKO");

        Map<String, String> mailMap = new HashMap<>();
        mailMap.put(Constants.ACTUAL_DATE, actualDate);
        mailMap.put(Constants.FILE_NAME, ingestionFlowDTO.getFileName());
        mailMap.put(Constants.TOTAL_ROWS_NUMBER, String.valueOf(ingestionFlowDTO.getTotalRowsNumber()));
         if (fileSize>0 && totalRowsNumber>0) {
            mailMap.put(Constants.MAIL_TEXT, StringSubstitutor.replace(templateOK, mailMap, "{", "}"));
        }
        else  {
            mailMap.put(Constants.MAIL_TEXT, StringSubstitutor.replace(templateKO, mailMap, "{", "}"));
        }

        return MailDTO.builder().params(mailMap).build();
    }

}
