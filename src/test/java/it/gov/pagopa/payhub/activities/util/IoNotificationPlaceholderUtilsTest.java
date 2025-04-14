package it.gov.pagopa.payhub.activities.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static it.gov.pagopa.payhub.activities.util.IoNotificationPlaceholderUtils.applyPlaceholder;

class IoNotificationPlaceholderUtilsTest {

    @Test
    void whenApplyPlaceholderWithCustomMapThenReplaceCorrectly() {
        // Given
        String markdownTemplate = "Ciao %name%, il tuo codice è %code%.";
        Map<String, String> placeholders = Map.of(
                "%name%", "Test",
                "%code%", "12345"
        );

        // When
        String result = applyPlaceholder(markdownTemplate, placeholders);

        // Then
        Assertions.assertEquals("Ciao Test, il tuo codice è 12345.", result);
    }
}
