package it.gov.pagopa.payhub.activities.util;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TreasuryUtils {

  static final String DATA_ORDINE = "\\s*D\\s*a\\s*t\\s*a\\s*O\\s*r\\s*d\\s*i\\s*n\\s*e\\s*:";
  static final String DESCRIZIONE_ORDINANTE = "\\s*D\\s*e\\s*s\\s*c\\s*r\\s*i\\s*z\\s*i\\s*o\\s*n\\s*e\\s*O\\s*r\\s*d\\s*i\\s*n\\s*a\\s*n\\s*t\\s*e\\s*:";

  static final public String BI2 = ":\\s*B\\s*I\\s*2\\s*:";
  static final public String BE1 = ":\\s*B\\s*E\\s*1\\s*:";
  static final public String IB1 = ":\\s*I\\s*B\\s*1\\s*:";
  static final public String IB2 = ":\\s*I\\s*B\\s*2\\s*:";
  static final public String IB4 = ":\\s*I\\s*B\\s*4\\s*:";
  static final public String TID = ":\\s*T\\s*I\\s*D\\s*:";
  static final public String DTE = ":\\s*D\\s*T\\s*E\\s*:";
  static final public String DTN = ":\\s*D\\s*T\\s*N\\s*:";
  static final public String ERI = ":\\s*E\\s*R\\s*I\\s*:";
  static final public String IM2 = ":\\s*I\\s*M\\s*2\\s*:";
  static final public String MA2 = ":\\s*M\\s*A\\s*2\\s*:";
  static final public String RI3 = ":\\s*R\\s*I\\s*3\\s*:";
  static final public String RI3_v2 = ":\\s*R\\s* I\\s*3\\s*:";
  static final public String OR1 = ":\\s*O\\s*R\\s*1\\s*:";
  static final public String SC2 = ":\\s*S\\s*C\\s*2\\s*:";
  static final public String TR1 = ":\\s*T\\s*R\\s*1\\s*:";
  static final public String SEC = ":\\s*S\\s*E\\s*C\\s*:";
  static final public String IOR = ":\\s*I\\s*O\\s*R\\s*:";

  static final String SEPARATORE_DUE_PUNTI = "[:]";
  static final String SEPARATORE_PUNTO_VIRGOLA = "[;]";

  static final public String PRE_IUF = "/PUR/LGPE-RIVERSAMENTO/URI/";
  static final public String PRE_IUV_RFS = "/RFS/";
  static final public String PRE_IUV_RFB = "/RFB/";

  public static final String PRE_IUF_NEW = "LGPE-RIVERSAMENTO";
  public static final String PRE_IUF_NEW_v2 = "LGPE- RIVERSAMENTO";
  public static final String PRE_IUF_NEW_v3 = "LGPE -RIVERSAMENTO";
  public static final String PRE_IUF_NEW_v4 = "LGPE - RIVERSAMENTO";
  public static final String PRE_IUF_NEW_v5 = "L GPE-RIVERSAMENTO";
  public static final String PRE_IUV_RFS_NEW = "RFS";
  public static final String PRE_IUV_RFB_NEW = "RFB";

  public static final String IUF = "IUF";
  public static final String IUV = "IUV";

  public static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";



  public static final String getIufValue(final String value) {
    if (StringUtils.isNotEmpty(value)) {
      String regexString = Pattern.quote(PRE_IUF) + "([A-Za-z0-9-_]+)";
      Pattern pattern = Pattern.compile(regexString);
      Matcher matcher = pattern.matcher(value);
      while (matcher.find()) {
        return matcher.group(1);
      }
    }
    return null;
  }

  public static final String getIuvValue(final String value) {

    String result = null;
    if (StringUtils.isNotEmpty(value)) {
      String regexStringRFB = Pattern.quote(PRE_IUV_RFB) + "(.*?)" + Pattern.quote("/");
      Pattern patternRFB = Pattern.compile(regexStringRFB);
      Matcher matcherRFB = patternRFB.matcher(value + "/");
      while (matcherRFB.find()) {
        result = matcherRFB.group(1);
      }

      if (StringUtils.isNotEmpty(result)) {
        return result;
      }

      String regexStringRFS = Pattern.quote(PRE_IUV_RFS) + "(.*?)" + Pattern.quote("/");
      Pattern patternRFS = Pattern.compile(regexStringRFS);
      Matcher matcherRFS = patternRFS.matcher(value);
      while (matcherRFS.find()) {
        result = matcherRFS.group(1);
      }
    }
    return result;
  }

  public static final Date getDataOrdine(final String value) {

    String regexString = DATA_ORDINE + "(.*?)" + SEPARATORE_PUNTO_VIRGOLA;
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(value);
    while (matcher.find()) {
      try {
        return new SimpleDateFormat("dd/MM/yyyy").parse(matcher.group(1).trim());
      } catch (ParseException e) {
        return null;
      }
    }
    return null;
  }

  public static final String getDescrizioneOrdinante(final String value) {

    String regexString = DESCRIZIONE_ORDINANTE + "(.*?)" + SEPARATORE_DUE_PUNTI;
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(value);
    while (matcher.find()) {
      return matcher.group(1).trim();
    }
    return null;
  }

  public static final String catchIdentificativo(final String value, final String code) throws Exception {

    if (value != null) {
      String regexString = code + "(.*?)" + SEPARATORE_DUE_PUNTI;
      Pattern pattern = Pattern.compile(regexString);
      Matcher matcher = pattern.matcher(value);
      while (matcher.find()) {
        return matcher.group(1).trim();
      }
    }
    return null;
  }

  public static final String getIdentificativo(String value, final String type) {
    value = value.replaceAll("/TXT/([0-9])/", "/");
    if (StringUtils.isNotBlank(value)) {
      if (type.equals(IUF)) {
        boolean acc = false;
        if (value.startsWith("ACCREDITI VARI")) {
          acc = true;
        }
        int indexIUF = value.indexOf(PRE_IUF_NEW);
        int indexIUF2 = value.indexOf(PRE_IUF_NEW_v2);
        int indexIUF3 = value.indexOf(PRE_IUF_NEW_v3);
        int indexIUF4 = value.indexOf(PRE_IUF_NEW_v4);
        int indexIUF5 = value.indexOf(PRE_IUF_NEW_v5);
        if (indexIUF != -1) {
          return elaboraIUF(value, indexIUF, acc, PRE_IUF_NEW);
        }

        if (indexIUF2 != -1) {
          return elaboraIUF(value, indexIUF2, acc, PRE_IUF_NEW_v2);
        }

        if (indexIUF3 != -1) {
          return elaboraIUF(value, indexIUF3, acc, PRE_IUF_NEW_v3);
        }

        if (indexIUF4 != -1) {
          return elaboraIUF(value, indexIUF4, acc, PRE_IUF_NEW_v4);
        }

        if (indexIUF5 != -1) {
          return elaboraIUF(value, indexIUF5, acc, PRE_IUF_NEW_v5);
        }
      }

      if (type.equals(IUV)) {
        int indexIUVRFB = value.indexOf(PRE_IUV_RFB_NEW);
        if (indexIUVRFB != -1) {
          // ho trovato uno iuv
          String result = null;
          String valueIUV = value.substring(indexIUVRFB);

          // IUV /RFB/RF...
          String regexStringRFB = Pattern.quote(PRE_IUV_RFB_NEW) + Pattern.quote("/") + "(.*?)"
                  + Pattern.quote("/");
          Pattern patternRFB = Pattern.compile(regexStringRFB);
          Matcher matcherRFB = patternRFB.matcher(valueIUV + "/");
          while (matcherRFB.find()) {
            result = matcherRFB.group(1);
          }

          if (StringUtils.isNotEmpty(result)) {
            return result;
          }

          // IUV RFB RF...
          regexStringRFB = Pattern.quote(PRE_IUV_RFB_NEW) + " (.*?)" + Pattern.quote(" ");
          patternRFB = Pattern.compile(regexStringRFB);
          matcherRFB = patternRFB.matcher(valueIUV + " ");
          while (matcherRFB.find()) {
            result = matcherRFB.group(1);
          }

          if (StringUtils.isNotEmpty(result)) {
            return result;
          }
        }

        int indexIUVRFS = value.indexOf(PRE_IUV_RFS_NEW);
        if (indexIUVRFS != -1) {
          // ho trovato uno iuv
          String result = null;
          String valueIUV = value.substring(indexIUVRFS);

          // IUV /RFS/RF...
          String regexStringRFS = Pattern.quote(PRE_IUV_RFS_NEW) + Pattern.quote("/") + "(.*?)"
                  + Pattern.quote("/");
          Pattern patternRFS = Pattern.compile(regexStringRFS);
          Matcher matcherRFS = patternRFS.matcher(valueIUV + "/");
          while (matcherRFS.find()) {
            result = matcherRFS.group(1);
          }

          if (StringUtils.isNotEmpty(result)) {
            if (result.length() > 25) {
              char[] charArr = result.toCharArray();
              int counter = 0;
              String iuvCorretto = "";
              for (char c : charArr) {
                if (counter == 25)
                  break;
                if (c != ' ') {
                  iuvCorretto += c;
                  counter++;
                }
              }
              result = iuvCorretto;
            }
            return result;
          }

          // IUV RFS RF...
          String iuvDaElaborare = valueIUV.substring(valueIUV.lastIndexOf(PRE_IUV_RFS_NEW) + 3);
          char[] charArr = iuvDaElaborare.toCharArray();
          int counter = 0;
          String iuvCorretto = "";
          for (char c : charArr) {
            if (counter == 25)
              break;
            if (c != ' ') {
              iuvCorretto += c;
              counter++;
            }
          }
          result = iuvCorretto;
          if (StringUtils.isNotBlank(result)) {
            return result;
          }
        }
      }
    }
    return null;
  }

  public static String getIdentificativoPSPDaIUF(final String value) {
    if (StringUtils.isNotBlank(value)) {
      String data = getDataFromIuf(value);
      if (StringUtils.isNotBlank(data)) {
        String valueSenzaData = StringUtils.remove(value, data);
        String regexString = "\\w+";
        Pattern pattern = Pattern.compile(regexString);
        Matcher matcher = pattern.matcher(valueSenzaData);
        while (matcher.find()) {
          return matcher.group(0);
        }
      }
    }
    return null;
  }

  public static String getDataFromIuf(final String value) {
    String regexString = DATE_PATTERN;
    Pattern pattern = Pattern.compile(regexString);
    Matcher matcher = pattern.matcher(value);
    while (matcher.find()) {
      return matcher.group(0);
    }
    return null;
  }

  public static boolean checkIufOld(final String value) {
    if (StringUtils.isNotBlank(value)) {
      String regexString = "([^a-zA-Z0-9\\d\\s:])";
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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
            + ((acc) ? "([A-Za-z0-9-_](\\S+)\\s+(\\S+))" : "([A-Za-z0-9-_]+)");
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