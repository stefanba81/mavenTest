package com.stefan;

import com.google.inject.Inject;
import com.stefan.modules.WebModule;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;

/**
 * Created by StefanB on 3/7/2017.
 */
public class UIBaseTest extends BaseTest{
    @Inject
    private WebDriver driver;
    @Inject
    protected WebDriverWait testWait;
    @Inject
    protected JavascriptExecutor jsExecutor;

    public UIBaseTest(){
        module = new WebModule();
    }

    /**
     * Shutdown driver object
     */
    @AfterClass(groups="driverSetup")
    public void tearDown() {
        if(proPrint != null)
            proPrint.appendInfo("Teardown driver");
        if (driver != null && !driver.toString().contains("(null)")) {
            driver.quit();
        }
    }

    /**
     * Delete all cookies from a particular domain
     * @param domain
     */
    protected void deleteCookiesByDomain(String domain) {
        this.getDriver().get("http://" + domain);
        this.getDriver().manage().deleteAllCookies();
        this.getDriver().navigate().refresh();
    }

//    /**
//     * Login via email by setting au cookie
//     * @param auToken authToken
//     */
//    protected void loginBySettingCookie(String auToken) {
//        setCookieAu(auToken, this.driver, tConfig.getServerUrl());
//        this.driver.navigate().refresh();
//        try {
//            Thread.sleep(1000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    protected void logoutByRemovingCookie() throws Exception {
//        removeCookie("au", driver);
//        driver.navigate().refresh();
//    }

    /**
     *
     * @return This test classes WebDriver
     */
    public WebDriver getDriver() {
        return driver;
    }
}
