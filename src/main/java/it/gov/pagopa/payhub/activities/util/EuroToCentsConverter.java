package it.gov.pagopa.payhub.activities.util;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

public class EuroToCentsConverter extends AbstractBeanField<Long, String> {
    @Override
    protected Object convert(String value) throws CsvDataTypeMismatchException {
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            String normalizedValue = value.replace(',', '.');
            BigDecimal euroAmount = new BigDecimal(normalizedValue);
            return euroAmount.multiply(BigDecimal.valueOf(100)).longValue();
        } catch (NumberFormatException e) {
            throw new CsvDataTypeMismatchException("Could not convert value '" + value + "' to cents");
        }
    }
}
