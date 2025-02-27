package it.gov.pagopa.payhub.activities.util;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.*;
import java.util.GregorianCalendar;
import java.util.Optional;
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
        return Optional.ofNullable(xmlGregorianCalendar)
            .map(xmlCal -> xmlCal.toGregorianCalendar().toZonedDateTime().toOffsetDateTime()
                .withOffsetSameInstant(ZONEID.getRules().getOffset(Instant.now())))
            .orElse(null);
    }

    public static OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return Optional.ofNullable(localDateTime)
            .map(ldt -> ldt.atZone(ZONEID).toOffsetDateTime()).orElse(null);
    }

    public static OffsetDateTime toOffsetDateTime(LocalDate localDate) {
        return Optional.ofNullable(localDate)
            .map(ld -> ld.atStartOfDay(ZONEID).toOffsetDateTime()).orElse(null);
    }
}