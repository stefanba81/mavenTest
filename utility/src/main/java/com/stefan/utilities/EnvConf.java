package com.stefan.utilities;

import junitx.util.PropertyManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: pritesh
 * Date: 3/15/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class EnvConf {

    private HashMap<String, String> envConf = new HashMap<String, String>();
    private HashMap<String, String> sysEnvConf = new HashMap<String, String>();
    private HashMap<String, String> sysPropConf = new HashMap<String, String>();

    private HashMap<String, String> masterMap;

    public EnvConf() {
        masterMap = new HashMap<>();

        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
        List<String> parameters = Arrays.asList(
                "API_SERVER",
                "API_VERSION",
                "BROWSER",
                "DATA_ENVIRONMENT",
                "DB_PASSWORD",
                "DB_SERVER",
                "DB_USERNAME",
                "DEBUG",
                "GRID_SERVER",
                "LOCALE",
                "LOG_RESULTS",
                "PD_RELEASE_ID",
                "PD_RELEASE_SERVER",
                "PD_RELEASE_TYPE",
                "PLATFORM",
                "RECO_SERVER",
                "SECURE",
                "SERVER",
                "SOLR",
                "TEST_PLAN_ID",
                "TIMEOUT",
                "USE_GRID",
                "IPA_FILE_NAME",
                "APK_FILE_NAME",
                "UDID",
                "DEVICE_NAME",
                "DEVICE_OS",
                "REDSHIFT_DB_USERNAME",
                "REDSHIFT_DB_PASSWORD",
                "INSTALL_APP"
        );
        for (String param : parameters) {
            // Parameters set by the local.properties file
            try {
                if (StringUtils.isNotEmpty(System.getProperty("PropertyManager.file")) && PropertyManager.getProperty(param) != null)
                    envConf.put(param, PropertyManager.getProperty(param));
            }catch(Exception ignored){}
                if (System.getenv(param) != null)
                    sysEnvConf.put(param, System.getenv(param));
                // These are the system property variables which can be passed in via Maven command line. EXAMPLE: -DAPI_VERSION=v5
                if (System.getProperty(param) != null)
                    sysPropConf.put(param, System.getProperty(param));
        }

        masterMap.putAll(getEnvConf());
        masterMap.putAll(System.getenv());
        masterMap.putAll(getSysPropConf());
    }

    public HashMap<String, String> getMasterMap(){
        return masterMap;
    }

    public void putAllToMasterMap(Map hashMap){
        masterMap.putAll(hashMap);
    }

    /**
     * Method returns the current env conf hashmap
     *
     * @return
     */
    public HashMap<String, String> getEnvConf() {
        return envConf;
    }

    /**
     * Resets the whole env conf map
     *
     * @param envConf
     */
    public void setEnvConf(HashMap<String, String> envConf) {
        this.envConf = envConf;
    }

    public HashMap<String, String> getSysPropConf() {
        return sysPropConf;
    }

    /**
     * This updates or create a new config into the hash
     *
     * @param key
     * @param value
     */
    public void setEnvConfEntry(String key, String value) {
        envConf.put(key, value);
    }

    /**
     * This Returns back a entry from the environment configuration
     *
     * @param key
     * @return
     */
    public String getEnvConfEntry(String key) {
        try {
            if (envConf.containsKey(key))
                return envConf.get(key);
        } catch (Exception e) {
            System.err.println("Key does not exist");
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * This Returns back a entry from the SYSTEM environment configuration
     *
     * @param key
     * @return
     */
    public String getSysEnvConfEntry(String key) {
        try {
            if (sysEnvConf.containsKey(key))
                return sysEnvConf.get(key);
        } catch (Exception e) {
            System.err.println("Key does not exist");
            System.err.println(e.getMessage());
        }
        return null;
    }

}