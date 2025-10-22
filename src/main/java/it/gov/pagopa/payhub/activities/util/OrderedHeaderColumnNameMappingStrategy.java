package it.gov.pagopa.payhub.activities.util;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;

public class OrderedHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        if (bean == null) {
            return super.generateHeader(null);
        }

        // Order the fields as they are declared in the DTO
        return Arrays.stream(bean.getClass().getDeclaredFields())
                .map(field -> {
                    var annotation = field.getAnnotation(com.opencsv.bean.CsvBindByName.class);
                    if (annotation != null && !annotation.column().isEmpty()) {
                        return annotation.column();
                    }
                    return field.getName();
                })
                .toArray(String[]::new);
    }
}
