package it.gov.pagopa.payhub.activities.utility;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TestUtils {

    public static final Date DATE = Date.from(
            LocalDateTime.of(2024, 5, 15, 10, 30, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
    );
}
