package it.gov.pagopa.payhub.activities.utility;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    private Utilities(){}
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$"; //RFC 5322
    public static final String ADDRESS_PATTERN = "[a-z A-Z0-9.,()/'&]{1,70}";
    public static final String NOT_ADDRESS_PATTERN = "[^a-z A-Z0-9.,()/'&]{1,70}";
    public static final String CIVIC_PATTERN = "[a-z A-Z0-9.,()/'&]{1,16}";
    public static boolean isValidEmail(final String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPostalCode(String postalCode, String isoNationCode) {
        // If nation = ITALIA the postal code must be numeric, else alphanumeric
        if ("IT".equalsIgnoreCase(isoNationCode)) {
            return postalCode.matches("^\\d{5}$");
        } else {
            return isValidPostalCode(postalCode);
        }
    }
    public static boolean isValidPostalCode(String postalCode) {
        return postalCode.matches("^[a-zA-Z0-9]{1,16}$");
    }

    public static boolean validateAddress(String address, boolean isPspPosteItaliane) {
        address = cleanUpAddress(address);
        if (isPspPosteItaliane) {
            Pattern pattern = Pattern.compile(ADDRESS_PATTERN);
            Matcher matcher = pattern.matcher(address);
            return matcher.matches();
        } else {
            for (int i = 0; i < address.length(); i++) {
                int ascii = address.charAt(i);
                if (ascii < 32 || ascii > 126) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String cleanUpAddress(String address) {
        String addressFixed = StringUtils.stripAccents(address);

        Pattern pattern = Pattern.compile(NOT_ADDRESS_PATTERN);
        Matcher matcher = pattern.matcher(addressFixed);
        addressFixed = matcher.replaceAll(" ");

        return addressFixed;
    }

    public static boolean validateCivic(String civic, boolean isPspPosteItaliane) {
        if (isPspPosteItaliane) {
            Pattern pattern = Pattern.compile(CIVIC_PATTERN);
            Matcher matcher = pattern.matcher(civic);
            return matcher.matches();
        } else {
            for (int i = 0; i < civic.length(); i++) {
                int ascii = civic.charAt(i);
                if (ascii < 32 || ascii > 126) {
                    return false;
                }
            }
            return true;
        }
    }
}