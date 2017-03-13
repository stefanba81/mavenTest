package stefan.ui.base;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.Set;

/**
 * Created by StefanB on 3/13/2017.
 */
public abstract class BasePage extends PageComponent {
    private String originalWindowTitle;

    /**
     * Setting the page's relative URL endpoint
     * @return
     */
    public abstract String getPageURL();

    /**
     * Opens the page to the value in {@link #getPageURL() getPageURL}
     */
    public void openPage() {
        String url = String.format("%s%s", tConfig.getServerUrl("com"), getPageURL());
        proPrint.appendInfo(String.format("Opening Page: %s", url));
        for(int i = 0; i < 2; i++) {
            driver.get(url);
            if(!driver.getPageSource().contains("Uh oh! The Kitchen's a mess!")){
                return;
            }
        }
    }

    /**
     * This switches to the window title you asked for
     *
     * @param windowTitle title of the window where you want to go
     */
    public String handleMultipleWindows(String windowTitle) {
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(windowTitle)) {
                return window;
            }
        }
        return "";
    }

    /**
     * Verifies if the given window title is displayed
     *
     * @param windowTitle
     * @return true or false if the window titles is on screen
     */
    public boolean verifyWindowTitle(String windowTitle) {
        boolean result = false;
        String parentWindow = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(windowTitle)) {
                proPrint.appendInfo("Opened page: " + driver.getCurrentUrl());
                result = true;
                driver.close();
                driver.switchTo().window(parentWindow);
                proPrint.appendInfo("Switch page back to: " + driver.getCurrentUrl());
            }
        }
        return result;
    }

    public boolean verifyWindowIsOnScreen(String windowTitle) {
        boolean result = false;
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(windowTitle)) {
                proPrint.appendInfo("Opened page: " + driver.getCurrentUrl());
                result = true;
            }
        }
        return result;
    }

    public void closeModal(String windowTitle){
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            WebDriver modal = driver.switchTo().window(window);
            if (driver.getTitle().contains(windowTitle)) {
                modal.close();
            }
        }
    }

    /**
     * This switches to the window url you asked for
     *
     * @param windowUrl Url of the window where you want to go
     */
    public void handleMultipleWindowsByUrl(String windowUrl) {
        Set<String> windows = driver.getWindowHandles();

        for (String window : windows) {
            try {
                driver.switchTo().window(window);
            } catch (NoSuchWindowException e) {
                proPrint.appendError(e.getLocalizedMessage() + " - Checking next window.");
                continue;
            }
            if (driver.getCurrentUrl().contains(windowUrl)) {
                return;
            }
        }
        windows.stream().forEach(proPrint::appendError);
    }

    /**
     * Saves the title of the current window so that you can go back later on once you done with the switched window
     */
    public void saveOriginalWindowTitle() {
        originalWindowTitle = driver.getTitle();
    }

    /**
     * Gives back the original title window
     *
     * @return
     */
    public String getOriginalWindowTitle() {
        return originalWindowTitle;
    }

    public void pageDown(){
        Actions action = new Actions(driver);
        action.sendKeys(Keys.PAGE_DOWN);
        action.perform();
    }

    public String jsScrollToElem() {
        return "arguments[0].scrollIntoView(true);";
    }

    public void jsScrollToElemBycssLocator(String cssSelector) {
        JavascriptExecutor js = ((JavascriptExecutor) driver);
        js.executeScript(String.format("document.querySelector('%s').scrollTop = %d", cssSelector, 1200));
    }
}
