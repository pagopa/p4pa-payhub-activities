package it.gov.pagopa.payhub.activities.service.files.xls;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class OrderedHeaderColumnNameMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    private String activeProfile;

    public void setActiveProfile(String profile) {
        this.activeProfile = profile;
    }

    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        if (bean == null) {
            return super.generateHeader(null);
        }

        super.generateHeader(bean);

        List<Field> declaredFields = Arrays.asList(bean.getClass().getDeclaredFields());

        List<String> orderedHeaders = declaredFields.stream()
                .filter(field -> !isIgnored(field))
                .map(field -> {
                    CsvBindByName ann = field.getAnnotation(CsvBindByName.class);
                    if (ann == null) return null;
                    return (ann.column() != null && !ann.column().isEmpty())
                            ? ann.column()
                            : field.getName();
                })
                .filter(Objects::nonNull)
                .toList();

        setColumnOrderOnWrite(Comparator.comparingInt(header -> {
            int idx = orderedHeaders.indexOf(header);
            return (idx == -1) ? Integer.MAX_VALUE : idx;
        }));

        headerIndex.initializeHeaderIndex(orderedHeaders.toArray(new String[0]));
        return orderedHeaders.toArray(new String[0]);
    }

    /**
     * Determine if a field should be ignored in base of @CsvIgnore and active profile
     */
    private boolean isIgnored(Field field) {
        CsvIgnore ignoreAnn = field.getAnnotation(CsvIgnore.class);
        if (ignoreAnn == null) {
            return false;
        }

        String[] profiles = ignoreAnn.profiles();
        if (profiles.length == 0 || Arrays.stream(profiles).allMatch(String::isBlank)) {
            return true;
        }

        if (activeProfile != null) {
            for (String profile : profiles) {
                if (profile.equals(activeProfile)) {
                    return true;
                }
            }
        }

        return false;
    }
}

