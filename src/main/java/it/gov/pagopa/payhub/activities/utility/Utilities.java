package it.gov.pagopa.payhub.activities.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    private Utilities(){}
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; //RFC 5322
    public static final int IBAN_LENGTH = 27;

    public static boolean isValidEmail(final String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
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
}