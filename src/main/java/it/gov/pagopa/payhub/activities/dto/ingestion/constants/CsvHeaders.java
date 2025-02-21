package it.gov.pagopa.payhub.activities.dto.ingestion.constants;

import java.util.List;

public class CsvHeaders {

    public static final List<String> INSTALLMENT_HEADERS = List.of(
            "File Name", "IUPD", "IUD", "Workflow Status", "Row Number", "Error Code", "Error Message"
    );

    public static final List<String> TREASURY_HEADERS = List.of(
            "FileName", "Anno Bolletta", "Codice Bolletta", "Error Code", "Error Message"
    );

    private CsvHeaders() {
    }
}
