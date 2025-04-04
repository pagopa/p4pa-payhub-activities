package it.gov.pagopa.payhub.activities.util;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class IoNotificationPlaceholderUtils {

    private IoNotificationPlaceholderUtils() {}

    public static final DateTimeFormatter ITALIAN_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String applyPlaceholder(String markdown, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            markdown = markdown.replace(entry.getKey(), entry.getValue());
        }
        return markdown.replaceAll("\\s{2,}", " ").trim();
    }
}
