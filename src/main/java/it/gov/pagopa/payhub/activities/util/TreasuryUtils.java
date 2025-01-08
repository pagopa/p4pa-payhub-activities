package it.gov.pagopa.payhub.activities.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

public class TreasuryUtils {
  private TreasuryUtils() {}

  public static final String IUF = "IUF";
  public static final String REGEX_MATCHER = "([A-Za-z0-9-_](\\S+)\\s+(\\S+))";
  public static final String ALPHANUM_PATTERN = "([A-Za-z0-9-_]+)";
  public static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
  public static final Pattern NOT_ALLOWED_CHARS_PATTERN = Pattern.compile("([^a-zA-Z\\d\\s:])");
  private static final List<String> PRE_IUF_PATTERNS = List.of(
          "LGPE-RIVERSAMENTO",
          "LGPE- RIVERSAMENTO",
          "LGPE -RIVERSAMENTO",
          "LGPE - RIVERSAMENTO",
          "L GPE-RIVERSAMENTO"
  );

  public static String getIdentificativo(String value, final String type) {
    if (StringUtils.isBlank(value)) {
      return null;
    }

    value = value.replaceAll("/TXT/(\\d)/", "/");

    if (StringUtils.isNotBlank(value) && type.equals(IUF)) {
      boolean acc = value.startsWith("ACCREDITI VARI");

      for (String pattern : PRE_IUF_PATTERNS) {
        int index = value.indexOf(pattern);
        if (index != -1) {
          return elaboraIUF(value, index, acc, pattern);
        }
      }
    }
    return null;
  }

  public static String getDataFromIuf(final String value) {
    Matcher matcher = DATE_PATTERN.matcher(value);
    return matcher.find() ? matcher.group(0) : null;
  }

  public static boolean checkIufOld(final String value) {
    if (StringUtils.isNotBlank(value)) {
      Matcher matcher = NOT_ALLOWED_CHARS_PATTERN.matcher(value);
      while (matcher.find()) {
        if (StringUtils.isNotBlank(matcher.group(0))) {
          return false;
        }
      }
    }
    return true;
  }

  private static String elaboraIUF(String value, int indexIUF, boolean acc, String patternString) {
    String[] regexTemplates = {
            Pattern.quote(patternString) + "/URI/(\\d{4}-\\d{2}- \\d{2})",
            Pattern.quote(patternString) + " URI (\\d{4}-\\d{2}- \\d{2})",
            Pattern.quote(patternString) + "/URI/(\\d{4}-\\d{2}-\\d{1} \\d{1})",
            Pattern.quote(patternString) + " URI (\\d{4}-\\d{2}-\\d{1} \\d{1})",
            Pattern.quote(patternString) + "/URI/",
            Pattern.quote(patternString) + " URI "
    };

    for (String regexTemplate : regexTemplates) {
      String regexString = regexTemplate + ((acc) ? REGEX_MATCHER : ALPHANUM_PATTERN);
      String result = extractIUF(value.substring(indexIUF), regexString);
      if (StringUtils.isNotBlank(result)) {
        return result;
      }
    }

    return null;
  }

  private static String extractIUF(String valueIUF, String regexString) {
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(valueIUF);
    while (matcher.find()) {
      String result = matcher.group(1);
      if (StringUtils.isNotBlank(result)) {
        result = StringUtils.deleteWhitespace(result);
        String date = getDataFromIuf(result);
        if (StringUtils.isNotBlank(date) || checkIufOld(result)) {
          return result;
        }
      }
    }
    return null;
  }
}
