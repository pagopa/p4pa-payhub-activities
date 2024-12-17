package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.utility.Utilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestUtils {
    private TestUtils(){}

    /**
     * It will assert not null on all o's fields
     */
    public static void checkNotNullFields(Object o, String... excludedFields) {
        Set<String> excludedFieldsSet = new HashSet<>(Arrays.asList(excludedFields));
        org.springframework.util.ReflectionUtils.doWithFields(o.getClass(),
                f -> {
                    f.setAccessible(true);
                    Assertions.assertNotNull(f.get(o), "The field "+f.getName()+" of the input object of type "+o.getClass()+" is null!");
                },
                f -> !excludedFieldsSet.contains(f.getName()));
    }

    public static final Date DATE = Date.from(
        LocalDateTime.of(2024, 5, 15, 10, 30, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
    );
}
