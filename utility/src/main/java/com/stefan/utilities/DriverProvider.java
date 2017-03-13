package com.stefan.utilities;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.openqa.selenium.remote.BrowserType.*;

/**
 * Created by StefanB on 3/8/2017.
 */
@Singleton
public class DriverProvider implements Provider<WebDriver> {

    private String ScreenSize;
    private String gridUrl;
    private final boolean useGrid;
    private final String browser;
    private final Integer timeout;
    private final Platform platform;

    @Inject
    public DriverProvider(@Named("USE_GRID") Boolean useGrid, @Named("PLATFORM") Platform platform, @Named("BROWSER") String browser, @Named("TIMEOUT") Integer timeout, @Named("GRID_SERVER") String gridHost){
        this.useGrid = useGrid;
        this.browser = browser;
        this.timeout = timeout;
        this.platform = platform;
        this.gridUrl = String.format("http://%s:%s/wd/hub", gridHost, "4444");
    }

    /**
     * Setups up the web driver object
     *
     * If you have set the environment variable to USE_GRID=true, then it will
     * try to connect to Selenium Grid and run the tests on that node.
     *
     * If you want to run it locally, it will try to run the browser you have
     * set in the ENV variable BROWSER
     */
    public WebDriver get(){
        WebDriver driver = createWebDriver();
        if (driver != null) {
            if (!browser.equals(SAFARI)) {
                driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
                //driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
            }
            if (StringUtils.isNotBlank(ScreenSize)) {
                Dimension dim = new Dimension(Integer.parseInt(ScreenSize), 768);
                driver.manage().window().setSize(dim);
            } else {
                driver.manage().window().maximize();
                ProPrint.info("Driver set to full screen - maximize.");
            }
        }
        return driver;
    }

    private WebDriver createWebDriver(){
        ProPrint.info("Setting up WebDriver Provider.");
        if (useGrid) {
            RemoteWebDriver driver = null;
            try {
                ProPrint.info(String.format("Attempting to connect to Selenium Grid: %s...", gridUrl));
                DesiredCapabilities browserCapabilities = generateDesiredCapabilities(platform, browser);
                ProPrint.info(String.format("Creating RemoteDriver for %s - %s...", platform, browser));
                return new RemoteWebDriver(new URL(gridUrl), browserCapabilities);
            } catch (Exception e) {
                if(driver != null){
                    ProPrint.warn("Unable to start up RemoteWebDriver properly. Going to attempt to call driver.quit()...");
                    driver.quit();
                }
                ProPrint.errorWithFail(String.format("Problem starting up Remote Browser. \n*** ERROR OCCURRED ***\n%s", e.getMessage()));
                return null;
            }
        } else {
            switch (browser) {
                case CHROME:
                    if(platform == Platform.WINDOWS)
                        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver.exe");
                    else
                        System.setProperty("webdriver.chrome.driver", "drivers/chromedriver");
                    return new ChromeDriver(getDefaultChromeOptions());
                case SAFARI:
                    return new SafariDriver();
                default:
                    System.out.println("Browser Type not recognized. Default to Firefox.");
                case FIREFOX:
                    if(platform == Platform.WINDOWS)
                        System.setProperty("webdriver.gecko.driver","drivers/geckodriver.exe");
                    else
                        System.setProperty("webdriver.gecko.driver","drivers/geckodriver");
                    return new FirefoxDriver();
            }
        }
    }

    /**
     * This method defines the capabilities of various browsers
     *
     * @param browserType type of browser requested
     * @return Capabilities based on browser
     */
    public static DesiredCapabilities generateDesiredCapabilities(Platform platform, String browserType) {
        DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
        desiredCapabilities.setBrowserName(browserType);
        desiredCapabilities.setPlatform(platform);
        desiredCapabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
//
//        String proxyIp = "localhost:8888";
//        Proxy proxy = new Proxy();
//        proxy.setHttpProxy(proxyIp);
//        proxy.setSslProxy(proxyIp);
//        proxy.setFtpProxy(proxyIp);
//
//        desiredCapabilities.setCapability(CapabilityType.PROXY, proxy);
//        desiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        switch (browserType) {
            case CHROME:
                desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, getDefaultChromeOptions());
                desiredCapabilities.setCapability("chrome.switches", Collections.singletonList("--no-default-browser-check"));
                HashMap<String, String> chromePreferences = new HashMap<>();
                chromePreferences.put("profile.password_manager_enabled", "false");
                desiredCapabilities.setCapability("chrome.prefs", chromePreferences);
                LoggingPreferences loggingPrefs = new LoggingPreferences();
                loggingPrefs.enable(LogType.BROWSER, Level.SEVERE);
                desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPrefs);
                break;
            case SAFARI:
                SafariOptions safariOptions = new SafariOptions();
                safariOptions.setUseCleanSession(true);
                desiredCapabilities.setCapability(SafariOptions.CAPABILITY, safariOptions);
                desiredCapabilities.setCapability("safari.cleanSession", true);
                break;
            case IE:
                desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                break;
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                desiredCapabilities.setCapability(EdgeOptions.CAPABILITY, edgeOptions);
                break;
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                desiredCapabilities.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
                break;
        }

        return desiredCapabilities;
    }

    /**
     * Creates the chrome options for our drivers
     * disables certain features that get in the way
     * of testing
     * @return ChromeOptions
     */
    private static ChromeOptions getDefaultChromeOptions(){
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("test-type");
        chromeOptions.addArguments("chrome.switches","--disable-extensions");
        return chromeOptions;
    }
}
