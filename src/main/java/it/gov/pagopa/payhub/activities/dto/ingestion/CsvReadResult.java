package it.gov.pagopa.payhub.activities.dto.ingestion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class CsvReadResult<T> {

    private Stream<T> dataStream;
    private long totalRows;

}
