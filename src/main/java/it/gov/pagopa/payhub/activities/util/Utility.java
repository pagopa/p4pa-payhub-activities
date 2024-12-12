package it.gov.pagopa.payhub.activities.util;

public class Utility {
    private Utility() {
    }
    public static boolean isNullOrEmptyString(String str){
        return(str == null || str.isEmpty());
    }
    public static boolean isNotNullOrEmpty(String str){
        return ! isNullOrEmptyString(str);
    }
}
