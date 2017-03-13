package com.stefan.data;

/**
 * Created by StefanB on 3/13/2017.
 */
public enum Locale {

    EN_US("en-US", "81.12.206.154"),
    EN_GB("en-GB", "78.33.166.0"),
    NL_NL("nl-NL", "77.60.41.216"),
    DE_DE("de-DE", "91.248.0.0"),
    DE_AT("de-AT", "77.116.0.0"),
    ZZ_ZZ("zz-ZZ", ""); //invalid locale

    final String value;
    final String ip;

    Locale(String value, String ip) {
        this.value = value;
        this.ip = ip;
    }

    public String getString() {
        return value;
    }

    public String getLanguageString() {
        return value.substring(0, 3) + "xx";
    }

    public String getIp() {
        return this.ip;
    }

    public String getCountryCode() {
        return this.value.substring(3);
    }

    public static String getIpByCountryCode(String countryCode) {
        String ip = "";
        for (Locale locale : Locale.values()) {
            String localeCC = locale.getCountryCode();
            if (localeCC.equals(countryCode)) {
                ip = locale.getIp();
            }
        }
        return ip;
    }
}
