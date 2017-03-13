package com.stefan.utilities;

import com.google.inject.Singleton;
import com.stefan.data.Locale;

/**
 * Created by StefanB on 3/7/2017.
 */
@Singleton
public interface TestConfigurationHandler {

    String getBrowser();

    Locale getLocale();

    boolean isDebug();

    void setDebug(Boolean debugMode);

    String getServer();

    String getApiVersion();
}
