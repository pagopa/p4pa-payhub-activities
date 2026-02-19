package it.gov.pagopa.payhub.activities.service.files;

import com.opencsv.CSVReader;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.IOException;
import java.util.Arrays;

public class RowAwareHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    private String[] originalHeader;

    @Override
    public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
        String[] header = reader.peek();
        if (header != null) {
            this.originalHeader = Arrays.copyOf(header, header.length);
        }

        super.captureHeader(reader);
    }

    public String[] getOriginalHeader() {
        return originalHeader == null
                ? null
                : Arrays.copyOf(originalHeader, originalHeader.length);
    }

    @Override
    public T populateNewBean(String[] row) throws CsvChainedException, CsvFieldAssignmentException {

        T bean = super.populateNewBean(row);

        if (bean instanceof CsvRowAware aware) {
            aware.setRow(Arrays.copyOf(row, row.length));
        }

        return bean;
    }
}
