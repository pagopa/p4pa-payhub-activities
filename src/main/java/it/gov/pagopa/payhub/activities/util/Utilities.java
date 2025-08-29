package it.gov.pagopa.payhub.activities.util;

import it.gov.pagopa.payhub.activities.dto.OffsetDateTimeIntervalFilter;
import it.gov.pagopa.pu.processexecutions.dto.generated.LocalDateIntervalFilter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    private Utilities(){}

    public static final ZoneId ZONEID = ZoneId.of("Europe/Rome");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    public static final int IBAN_LENGTH = 27;
    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final DatatypeFactory DATATYPE_FACTORY_XML_GREGORIAN_CALENDAR;

    static {
        try {
            DATATYPE_FACTORY_XML_GREGORIAN_CALENDAR = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static boolean isValidEmail(final String email) {
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidIban(String iban) {
        return iban != null && iban.length() == IBAN_LENGTH;
    }

    public static boolean isValidPIVA(String pi) {
        int i;
        int c;
        int s;
        if (pi.isEmpty())
            return false;
        if (pi.length() != 11)
            return false;
        for (i = 0; i < 11; i++) {
            if (pi.charAt(i) < '0' || pi.charAt(i) > '9')
                return false;
        }
        s = 0;
        for (i = 0; i <= 9; i += 2)
            s += pi.charAt(i) - '0';
        for (i = 1; i <= 9; i += 2) {
            c = 2 * (pi.charAt(i) - '0');
            if (c > 9)
                c = c - 9;
            s += c;
        }
        return (10 - s % 10) % 10 == pi.charAt(10) - '0';
    }

    /** It will remove and replace file extension */
    public static String replaceFileExtension(String fileName, String newExtension){
        return fileName.substring(0, fileName.lastIndexOf(".")) + newExtension;
    }

    public static LocalDate convertToLocalDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }

        GregorianCalendar gregorianCalendar = xmlGregorianCalendar.toGregorianCalendar();

        return LocalDate.ofInstant(
                gregorianCalendar.toInstant(),
                gregorianCalendar.getTimeZone().toZoneId()
        );
    }

    public static Long bigDecimalEuroToLongCentsAmount(BigDecimal euroAmount) {
        return euroAmount != null ? euroAmount.multiply(HUNDRED).longValue() : null;
    }

    public static XMLGregorianCalendar toXMLGregorianCalendar(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? DATATYPE_FACTORY_XML_GREGORIAN_CALENDAR.newXMLGregorianCalendar(GregorianCalendar.from(offsetDateTime.toZonedDateTime())) : null;
    }

    public static OffsetDateTime toOffsetDateTime(XMLGregorianCalendar xmlGregorianCalendar) {
	    if (xmlGregorianCalendar == null) {
		    return null;
	    }
	    return xmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toOffsetDateTime()
	        .withOffsetSameInstant(ZONEID.getRules().getOffset(Instant.now()));
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
	    if (localDateTime == null) {
		    return null;
	    }
	    return localDateTime.atZone(ZONEID).toOffsetDateTime();
    }

    public static OffsetDateTime toOffsetDateTimeStartOfTheDay(LocalDate localDate) {
	    if (localDate == null) {
		    return null;
	    }
	    return localDate.atStartOfDay(ZONEID).toOffsetDateTime();
    }

    public static OffsetDateTime toOffsetDateTimeEndOfTheDay(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        LocalDateTime endOfDay = LocalDateTime.of(localDate, LocalTime.MAX.truncatedTo(java.time.temporal.ChronoUnit.MILLIS));
        return endOfDay.atZone(ZONEID).toOffsetDateTime();
    }

    /** It will convert a {@link LocalDateIntervalFilter} into an {@link OffsetDateTimeIntervalFilter} configured from the start of the <i>from</i> day to the end of the <i>to</i> day */
    public static OffsetDateTimeIntervalFilter toRangeClosedOffsetDateTimeIntervalFilter(LocalDateIntervalFilter localDateIntervalFilter){

       return OffsetDateTimeIntervalFilter.builder()
                .from(localDateIntervalFilter != null ? toOffsetDateTimeStartOfTheDay(localDateIntervalFilter.getFrom()): null)
                .to(localDateIntervalFilter != null ? toOffsetDateTimeEndOfTheDay(localDateIntervalFilter.getTo()): null)
                .build();
    }

    public static String removePiiFromURI(URI uri){
        return uri != null
                ? uri.toString().replaceAll("=[^&]*", "=***")
                : null;
    }

    public static BigDecimal longCentsToBigDecimalEuro(Long centsAmount) {
        return centsAmount != null ? BigDecimal.valueOf(centsAmount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN) : null;
    }

    public static String parseBigDecimalToString(BigDecimal importo) {
        if (importo == null) {
            return null;
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
        symbols.setDecimalSeparator(',');
        symbols.setGroupingSeparator('.');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        return decimalFormat.format(importo);
    }

    public static String centsAmountToEuroString(Long centsAmount){
        return parseBigDecimalToString(longCentsToBigDecimalEuro(centsAmount));
    }
}
