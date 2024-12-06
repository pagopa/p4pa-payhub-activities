package it.gov.pagopa.payhub.activities.activity.utility.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String MAIL_TEXT = "mailText";
    public static final String ACTUAL_DATE = "actualDate";
    public static final String FILE_NAME = "fileName";
    public static final String TOTAL_ROWS_NUMBER = "totalRowsNumber";
    public static final String DATE_MAILFORMAT = "EEE, MMM dd yyyy, hh:mm:ss";
    public static final DateTimeFormatter MAIL_DATE_FORMAT =  DateTimeFormatter.ofPattern("EEE, MMM dd yyyy, hh:mm:ss");
}
