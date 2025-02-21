package it.gov.pagopa.payhub.activities.dto.ingestion;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class CsvReadResult<T> {
    private final Stream<T> dataStream;
    private final AtomicLong totalRows;

    public long getTotalRows() {
        return totalRows.get();
    }
}

