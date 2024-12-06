package it.gov.pagopa.payhub.activities.config;

import lombok.Data;

@Data
public class EmailTemplate{
    private String subject;
    private String body;
}