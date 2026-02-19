package it.gov.pagopa.payhub.activities.service.files;

public interface CsvHeaderAware {

    void setOriginalHeader(String[] header);

    String[] getOriginalHeader();
}
