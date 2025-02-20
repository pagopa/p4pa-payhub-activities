package it.gov.pagopa.payhub.activities.util;

import com.opencsv.bean.AbstractBeanField;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class CsvOffsetDateTimeConverter extends AbstractBeanField<OffsetDateTime, String> {

    @Override
    protected OffsetDateTime convert(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDate.parse(value).atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}