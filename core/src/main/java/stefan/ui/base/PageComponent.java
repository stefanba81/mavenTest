package stefan.ui.base;

import com.google.inject.Inject;
import com.stefan.utilities.Initializer;
import com.stefan.utilities.ProPrint;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import stefan.TestConfiguration;

import java.util.List;

import static org.testng.Assert.*;

/**
 * Created by StefanB on 3/13/2017.
 */
public class PageComponent implements Initializer{
    @Inject
    protected TestConfiguration tConfig;
    @Inject
    protected ProPrint proPrint;
    @Inject
    protected WebDriver driver;
    @Inject
    protected WebDriverWait _wait;
    @Inject
    protected JavascriptExecutor _jsExecutor;

    /**
     * This runs post injection
     */
    @Override
    public void init(){
        PageFactory.initElements(driver, this);
    }

    public List<WebElement> findAllByCssSelector(String locator){
        return driver.findElements(By.cssSelector(locator));
    }

    /**
     * Find an element via CssSelector
     * @param locator
     * @return
     */
    public WebElement findByCssSelector(String locator){
        return driver.findElement(By.cssSelector(locator));
    }

    /**
     * Find an element via XPATH
     * @param locator
     * @return
     */
    public WebElement findByXPathSelector(String locator){
        return driver.findElement(By.xpath(locator));
    }

    /**
     * Waits for the element to be displayed. Try for 10 times max.Sleep 1 seonds in between tries
     *
     * @param webElement
     * @throws Exception
     */
    public void waitForElementPresent(WebElement webElement) throws Exception {
        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            try {
                if (webElement.isDisplayed()) {
                    return;
                }
            } catch (NoSuchElementException e) {
                Thread.sleep(1000);
            }
        }
        proPrint.appendErrorWithFail("Unable to find element after: " + maxRetries + " tries.");
    }

    public void jsClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        assertNotNull(element.findElement(By.xpath(".")), String.format("jsClick: could not find %s", element));
        Object o = executor.executeScript("try { arguments[0].click(); return true; } catch (e) { return false; } ", element);

        if (o instanceof Boolean) {
            assertTrue((Boolean) o, String.format("jsClick: JS executor returned false for %s", element));

            if ((Boolean) o) {
                proPrint.appendInfo(String.format("jsClick: success on %s", element));
            }
        } else {
            fail(String.format("jsClick: JS executor did not return a boolean for %s", element));
        }
    }

    /**
     * Clicks on an element
     *
     * @param element
     * @param properName
     */
    protected void clickElement(WebElement element, String properName) {
        try {
            _wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
            proPrint.appendInfo("Clicked element: " + properName);
            // Wait after a click occurs for time to load the page
            Thread.sleep(1500L);
        } catch (NoSuchElementException e) {
            proPrint.appendErrorWithFail("Unable to find '" + properName + "' to be clicked.", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clicks on an element by passing the cssSelector String
     *
     * @param locator
     * @param properName
     */
    protected void clickElement(String locator, String properName) {
        try {
            _wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(locator)));
            findByCssSelector(locator).click();
            proPrint.appendInfo("Clicked element: " + properName);
        } catch (NoSuchElementException e) {
            proPrint.appendErrorWithFail("Unable to find '" + properName + "' to be clicked.", e);
        }
    }

    /**
     * Clears the value from element and sets text
     *
     * @param element
     * @param text
     */
    protected void setText(WebElement element, String text) {
        try {
            _wait.until(visibilityOfElementLocated(element));
            element.clear();
            element.sendKeys(text);
            assertEquals(element.getAttribute("value"), text);
            proPrint.appendInfo("Set text as: " + text);
        } catch (NoSuchElementException e) {
            proPrint.appendErrorWithFail("Unable to find element for input text: '" + text + "'.", e);
        } catch (AssertionError e1) {
            proPrint.appendErrorWithFail("Text was not properly set.", e1);
        }
    }

    public boolean isElementPresent(WebElement element){
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e1) {
            return false;
        } catch (TimeoutException e2) {
            return false;
        }
    }

    public boolean isVerticalScroll(WebElement element){
        return (Boolean) _jsExecutor.executeScript("return arguments[0].scrollHeight>arguments[0].clientHeight;", element);
    }

    //Waits

    public static ExpectedCondition<Boolean> invisibilityOfElementLocated(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return !(element.isDisplayed());
                } catch (NoSuchElementException e) {
                    // Returns true because the element is not present in DOM. The
                    // try block checks if the element is present but is invisible.
                    return true;
                } catch (StaleElementReferenceException e) {
                    // Returns true because stale element reference implies that element
                    // is no longer visible.
                    return true;
                }
            }

            @Override
            public String toString() {
                return "element to no longer be visible: " + element;
            }
        };
    }

    protected ExpectedCondition<Boolean> visibilityOfElementLocated(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return element.isDisplayed();
                } catch (NoSuchElementException e) {
                    // Returns false because the element is not present in DOM. The
                    // try block checks if the element is present but is invisible.
                    return false;
                } catch (StaleElementReferenceException e) {
                    // Returns false because stale element reference implies that element
                    // is no longer visible.
                    return false;
                }
            }

            @Override
            public String toString() {
                return "element to be visible: " + element;
            }
        };
    }

}
