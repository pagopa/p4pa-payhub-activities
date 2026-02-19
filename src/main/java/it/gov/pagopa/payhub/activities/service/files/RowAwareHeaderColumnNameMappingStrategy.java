package it.gov.pagopa.payhub.activities.service.files;

import com.opencsv.CSVReader;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvChainedException;
import com.opencsv.exceptions.CsvFieldAssignmentException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.Getter;

import java.io.IOException;

@Getter
public class RowAwareHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    private String[] originalHeader;

    @Override
    public void captureHeader(CSVReader reader) throws IOException, CsvRequiredFieldEmptyException {
        this.originalHeader = reader.peek();
        super.captureHeader(reader);
    }

    @Override
    public T populateNewBean(String[] row) throws CsvChainedException, CsvFieldAssignmentException {
        T bean = super.populateNewBean(row);

        if (bean instanceof CsvRowAware aware) {
            aware.setRow(row);
        }

        return bean;
    }
}
