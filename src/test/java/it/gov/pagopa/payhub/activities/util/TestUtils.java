package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Assertions;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.common.ManufacturingContext;
import uk.co.jemos.podam.typeManufacturers.AbstractTypeManufacturer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class TestUtils {
    private TestUtils(){}

    public static final LocalDateTime LOCALDATETIME = LocalDateTime.of(2024, 5, 15, 10, 30, 0);
    public static final OffsetDateTime OFFSETDATETIME = ZonedDateTime.of(LOCALDATETIME, ZoneId.of("Europe/Rome")).toOffsetDateTime();
    public static final Date DATE = Date.from(LOCALDATETIME
            .atZone(ZoneId.systemDefault())
            .toInstant()
    );

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

    public static PodamFactory getPodamFactory() {
        PodamFactoryImpl podamFactory = new PodamFactoryImpl();
        podamFactory.getStrategy().addOrReplaceTypeManufacturer(SortedSet.class, new AbstractTypeManufacturer<>(){
            @Override
            public SortedSet<?> getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata, ManufacturingContext manufacturingCtx) {
                return new TreeSet<>();
            }
        });
        return podamFactory;
    }

}
