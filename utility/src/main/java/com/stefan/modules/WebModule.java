package com.stefan.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.stefan.utilities.DriverProvider;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by StefanB on 3/13/2017.
 */
public class WebModule extends TestingModule{
    public WebModule(ITestContext contextParams) {
        super(contextParams);
    }

    public WebModule() {
        super();
    }

    @Override
    public void runDriverBindings() {
        envMap.entrySet().stream().filter(env -> env.getValue() != null).forEachOrdered(env -> {

        });

        bind(WebDriver.class).toProvider(DriverProvider.class).in(Singleton.class);
        bind(WebDriverWait.class).toProvider(new Provider<WebDriverWait>() {
            @Inject
            WebDriver driver;
            @Inject @Named("TIMEOUT") Integer timeout;
            @Override
            public WebDriverWait get() {
                return new WebDriverWait(driver, timeout);
            }
        });
        bind(JavascriptExecutor.class).toProvider(new Provider<JavascriptExecutor>() {
            @Inject WebDriver driver;
            @Override
            public JavascriptExecutor get() {
                return ((JavascriptExecutor) driver);
            }
        });
    }
}
