package it.gov.pagopa.payhub.activities.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class MailParams implements Serializable {
    Map<String, String> params;
    String ingestionFlowId;
    String emailFromAddress;
    String emailFromName;
    String templateName;
    String mailSubject;
    String htmlText;
    boolean success;
    String[] to;
    String[] cc;
}
