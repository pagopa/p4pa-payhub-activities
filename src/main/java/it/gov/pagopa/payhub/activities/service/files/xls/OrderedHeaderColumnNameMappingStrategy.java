package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;

public class OrderedHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        if (bean == null) {
            return super.generateHeader(null);
        }

        super.generateHeader(bean);

        // Return the fields in the order they are declared inside the DTO
        return Arrays.stream(bean.getClass().getDeclaredFields())
                .map(field -> {
                    CsvBindByName annotation = field.getAnnotation(CsvBindByName.class);
                    if (annotation != null && !annotation.column().isEmpty()) {
                        return annotation.column();
                    }
                    return field.getName();
                })
                .toArray(String[]::new);
    }
}
