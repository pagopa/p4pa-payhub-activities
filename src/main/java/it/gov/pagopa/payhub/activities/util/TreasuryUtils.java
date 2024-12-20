package it.gov.pagopa.payhub.activities.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TreasuryUtils {
    private TreasuryUtils() {}

  public static final String PRE_IUF_NEW = "LGPE-RIVERSAMENTO";
  public static final String PRE_IUF_NEW_V2 = "LGPE- RIVERSAMENTO";
  public static final String PRE_IUF_NEW_V3 = "LGPE -RIVERSAMENTO";
  public static final String PRE_IUF_NEW_V4 = "LGPE - RIVERSAMENTO";
  public static final String PRE_IUF_NEW_V5 = "L GPE-RIVERSAMENTO";


  public static final String IUF = "IUF";

  public static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";
  public static final String REGEX_MATCHER = "([A-Za-z0-9-_](\\S+)\\s+(\\S+))";
  public static final String ALPHANUM_PATTERN = "([A-Za-z0-9-_]+)";

  public static String getIdentificativo(String value, final String type) {
    if(StringUtils.isBlank(value))
      return null;
    value = value.replaceAll("/TXT/(\\d)/", "/");
    if (StringUtils.isNotBlank(value) && type.equals(IUF)) {
        boolean acc = false;
        if (value.startsWith("ACCREDITI VARI")) {
          acc = true;
        }
        int indexIUF = value.indexOf(PRE_IUF_NEW);
        int indexIUF2 = value.indexOf(PRE_IUF_NEW_V2);
        int indexIUF3 = value.indexOf(PRE_IUF_NEW_V3);
        int indexIUF4 = value.indexOf(PRE_IUF_NEW_V4);
        int indexIUF5 = value.indexOf(PRE_IUF_NEW_V5);
        if (indexIUF != -1) {
          return elaboraIUF(value, indexIUF, acc, PRE_IUF_NEW);
        }

        if (indexIUF2 != -1) {
          return elaboraIUF(value, indexIUF2, acc, PRE_IUF_NEW_V2);
        }

        if (indexIUF3 != -1) {
          return elaboraIUF(value, indexIUF3, acc, PRE_IUF_NEW_V3);
        }

        if (indexIUF4 != -1) {
          return elaboraIUF(value, indexIUF4, acc, PRE_IUF_NEW_V4);
        }

        if (indexIUF5 != -1) {
          return elaboraIUF(value, indexIUF5, acc, PRE_IUF_NEW_V5);
        }
    }
    return null;
  }


  public static String getDataFromIuf(final String value) {
    String regexString = DATE_PATTERN;
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(value);

    return matcher.find() ? matcher.group(0) : null;
  }

  public static boolean checkIufOld(final String value) {
    if (StringUtils.isNotBlank(value)) {
      String regexString = "([^a-zA-Z\\d\\s:])";
      Pattern pattern = Pattern.compile(regexString);
      Matcher matcher = pattern.matcher(value);
      while (matcher.find()) {
        String finalValue = matcher.group(0);
        if (StringUtils.isNotBlank(finalValue)) {
          return false;
        }
      }
    }
    return true;
  }

  private static String elaboraIUF(String value, int indexIUF, boolean acc, String patternString) {

    String valueIUF = value.substring(indexIUF);
    String result = null;
    String regexString = Pattern.quote(patternString) + "/URI/" + "(\\d{4}-\\d{2}- \\d{2})"
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = matcher.group(1) + matcher.group(2);
    }

    if (StringUtils.isNotBlank(result)) {
      result = StringUtils.deleteWhitespace(result);
      String date = getDataFromIuf(result);
      boolean checkIUFVecchio = checkIufOld(result);
      if (StringUtils.isNotBlank(date) || checkIUFVecchio) {
        return result;
      }
    }

    valueIUF = value.substring(indexIUF);
    result = null;
    regexString = Pattern.quote(patternString) + " URI " + "(\\d{4}-\\d{2}- \\d{2})"
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    pattern = Pattern.compile(regexString);
    matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = matcher.group(1) + matcher.group(2);
    }

    if (StringUtils.isNotBlank(result)) {
      result = StringUtils.deleteWhitespace(result);
      String date = getDataFromIuf(result);
      boolean checkIUFVecchio = checkIufOld(result);
      if (StringUtils.isNotBlank(date) || checkIUFVecchio) {
        return result;
      }
    }

    valueIUF = value.substring(indexIUF);
    result = null;
    regexString = Pattern.quote(patternString) + "/URI/" + "(\\d{4}-\\d{2}-\\d{1} \\d{1})"
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    pattern = Pattern.compile(regexString);
    matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = matcher.group(1) + matcher.group(2);
    }

    if (StringUtils.isNotBlank(result)) {
      result = StringUtils.deleteWhitespace(result);
      String date = getDataFromIuf(result);
      boolean checkIUFVecchio = checkIufOld(result);
      if (StringUtils.isNotBlank(date) || checkIUFVecchio) {
        return result;
      }
    }

    valueIUF = value.substring(indexIUF);
    result = null;
    regexString = Pattern.quote(patternString) + " URI " + "(\\d{4}-\\d{2}-\\d{1} \\d{1})"
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    pattern = Pattern.compile(regexString);
    matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = matcher.group(1) + matcher.group(2);
    }

    if (StringUtils.isNotBlank(result)) {
      result = StringUtils.deleteWhitespace(result);
      String date = getDataFromIuf(result);
      boolean checkIUFVecchio = checkIufOld(result);
      if (StringUtils.isNotBlank(date) || checkIUFVecchio) {
        return result;
      }
    }

    // ho trovato uno iuf
    valueIUF = value.substring(indexIUF);
    result = null;
    regexString = Pattern.quote(patternString) + "/URI/"
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    pattern = Pattern.compile(regexString);
    matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = matcher.group(1);
    }

    if (StringUtils.isNotBlank(result)) {
      if (acc) {
        result = StringUtils.deleteWhitespace(result);
      }
      String date = getDataFromIuf(result);
      boolean checkIUFVecchio = checkIufOld(result);
      if (StringUtils.isNotBlank(date) || checkIUFVecchio) {
        return result;
      }
    }

    // ho spazi al posto degli slash
    regexString = Pattern.quote(patternString) + " URI "
            + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
    pattern = Pattern.compile(regexString);
    matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      result = StringUtils.deleteWhitespace(matcher.group(1));
    }

    if (StringUtils.isNotBlank(result)) {
      if (acc) {
        result = StringUtils.deleteWhitespace(result);
      }
      String date = getDataFromIuf(result);
      if (StringUtils.isNotBlank(date)) {
        return result;
      }
    }

    return null;
  }

}