package it.gov.pagopa.payhub.activities.model;

import it.gov.pagopa.payhub.activities.dto.reportingflow.IngestionFlowDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;
import java.util.Map;

/**
 * utility class to manage mail parameters
 */
@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
@Lazy
public class MailParams implements Serializable {
    IngestionFlowDTO ingestionFlowDTO;
    Map<String, String> params;
    String emailFromAddress;
    String emailFromName;
    String templateName;
    String mailSubject;
    String mailText;
    String htmlText;
    boolean success;
    String[] to;
    String[] cc;
    String id;
}
