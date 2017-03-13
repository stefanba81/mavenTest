package stefan;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.stefan.data.Locale;
import com.stefan.utilities.TestConfigurationHandler;

/**
 * Created by StefanB on 3/8/2017.
 */
@Singleton
public class TestConfiguration implements TestConfigurationHandler {

    @Inject
    @Named("SECURE")
    String protocol;
    @Inject
    @Named("SERVER")
    String host;
    @Inject
    @Named("LOCALE")
    String locale;
    @Inject
    @Named("USE_GRID")
    Boolean useGrid;
    @Inject
    @Named("BROWSER")
    String browserType;
    @Inject
    @Named("GRID_SERVER")
    String gridServer;
    @Inject
    @Named("TIMEOUT")
    Integer baseTimeout;
    @Inject
    @Named("DEBUG")
    String debug;
    @Inject
    @Named("LOG_RESULTS")
    Boolean logResults;
    @Inject
    @Named("TEST_PLAN_ID")
    String testPlanId;
    @Inject
    @Named("API_VERSION")
    String apiVersion;
    @Inject
    @Named("DATA_ENVIRONMENT")
    String dataEnvironment;

    public String getBrowser() {
        return browserType;
    }

    public String getServerUrl() {
        return getServerUrl(getTopLevelDomain());
    }

    public String getServerUrl(String TLD) {
        String serverUrl = getProtocol() + host;
        serverUrl = serverUrl.replaceAll("demoqa\\.(co|m)$", String.format("demoqa.%s", TLD));

        return serverUrl;
    }

    public String getTopLevelDomain(){
        switch (getLocale()) {
            case EN_GB:
                return "uk";
            case NL_NL:
                return "nl";
            case DE_DE:
                return "de";
            default:
                return "co";
        }
    }

    public String getAPIDomain() {
        return "com";
    }

    public Locale getLocale() {
        for (Locale l : Locale.values()) {
            if (l.getString().toLowerCase().equals(locale.toLowerCase())) {
                return l;
            }
        }
        throw new NullPointerException("Unable to find locale value in set locales");
    }

    public String getProtocolSecure() {
        return this.getProtocol();
    }

    public String getProtocol() {
        return (protocol != null && protocol.equalsIgnoreCase("true")) ? "https://" : "http://";
    }

    public void setProtocolSecure(Boolean protocol) {
        this.protocol = protocol.toString();
    }

    public Boolean getUseGrid() {
        return this.useGrid;
    }

    public String getGridUrl() {
        return "http://" + this.gridServer + ":4444/wd/hub";
    }

    public int getBaseTimeout() {
        return this.baseTimeout;
    }

    public boolean isDebug() {
        return (this.debug != null && this.debug.equalsIgnoreCase("true"));
    }

    public void setDebug(Boolean debugMode) {
        this.debug = debugMode.toString();
    }

    public Boolean getLogResults() {
        return this.logResults;
    }

    public String getTestPlanId() {
        return this.testPlanId;
    }

    /**
     *
     * @return set value for SERVER. Example: test.stefan.com
     */
    public String getServer() {
        return this.host;
    }

    public void setHost(String value) {
        this.host = value;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getBentoName() {
        return getSubDomain(2);
    }

    public String getDataEnv() {
        return dataEnvironment;
    }

    String getSubDomain(int offset){
        String subDomain = "";
        String[] splitServer = host.split("\\.");
        for(int i = 0; i < splitServer.length; i++){
            if(splitServer[i].equalsIgnoreCase("demoqa")){
                subDomain = splitServer[i-offset];
            }
        }
        return subDomain;
    }
}
