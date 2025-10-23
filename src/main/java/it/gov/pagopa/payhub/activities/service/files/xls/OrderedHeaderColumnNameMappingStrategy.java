package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class OrderedHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {
    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        if (bean == null) {
            return super.generateHeader(null);
        }

        super.generateHeader(bean);

        List<String> orderedHeaders = Stream.of(bean.getClass().getDeclaredFields())
                .map(field -> {
                    CsvBindByName ann = field.getAnnotation(CsvBindByName.class);
                    if (ann == null) return null;
                    return (ann.column() != null && !ann.column().isEmpty()) ? ann.column() : field.getName();
                })
                .filter(Objects::nonNull)
                .toList();

        setColumnOrderOnWrite(Comparator.comparingInt(header -> {
            int idx = orderedHeaders.indexOf(header);
            return (idx == -1) ? Integer.MAX_VALUE : idx;
        }));

        return orderedHeaders.toArray(String[]::new);
    }
}
