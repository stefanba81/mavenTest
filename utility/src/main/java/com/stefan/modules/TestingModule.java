package com.stefan.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import com.stefan.utilities.Initializer;
import com.stefan.utilities.ProPrint;
import com.stefan.utilities.EnvConf;
import org.apache.maven.shared.utils.StringUtils;
import org.testng.ITestContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by StefanB on 3/7/2017.
 */
public class TestingModule extends AbstractModule {

    private Map<String, String> contextParams;
    HashMap<String, String> envMap;

    public TestingModule(ITestContext contextParams) {
        this.contextParams = contextParams.getCurrentXmlTest().getAllParameters();
    }

    public TestingModule() {
        this.contextParams = new HashMap<>();
    }

    @Override
    protected void configure() {
        EnvConf envConf = new EnvConf();

        envConf.putAllToMasterMap(this.contextParams);
        envMap = envConf.getMasterMap();
        /* If there are any variables that have not been set, this will give a
            default value. Some of the more important ones, will give you #MISSING_PARAM
            Those are required for running
        */
        setDefault("API_SERVER", envMap.get("SERVER"));
        setDefault("DATA_ENVIRONMENT", "#MISSING_DATA_ENVIRONMENT_VAR");
        setDefault("DB_SERVER", envMap.get("SERVER"));
        setDefault("DB_USERNAME", "#MISSING_DB_USERNAME");
        setDefault("DB_PASSWORD", "#MISSING_DB_PASSWORD");
        setDefault("SOLR", envMap.get("SERVER"));
        setDefault("RECO_SERVER", envMap.get("SERVER"));
        setDefault("API_VERSION", "v9");
        setDefault("GRID_SERVER", "jenkins.yummly.com");
        setDefault("USE_GRID", "false");
        setDefault("LOCALE", "en-US");
        setDefault("TIMEOUT", "6");
        setDefault("SECURE", "false");
        setDefault("DEBUG", "false");
        setDefault("SCREEN_SIZE", "");
        setDefault("RECO_SERVER", "#MISSING_RECO_SERVER_PARAM");
        setDefault("LOG_RESULTS", "false");
        setDefault("TEST_PLAN_ID", "0");

        /*
        * For each of the variables, this Module will bind the key/value to a class type.
        * In particular instances, it will parse in the object time like Boolean or BrowserType
        * This will just be defaulting to Strings.
        */
        envMap.entrySet().stream().filter(env -> env.getValue() != null).forEachOrdered(env -> {
            if (env.getKey().equals("USE_GRID")) {
                bind(Boolean.class).annotatedWith(Names.named(env.getKey())).toInstance(
                        Boolean.parseBoolean(env.getValue()));
            } else if (env.getKey().equals("TIMEOUT")) {
                bind(Integer.class).annotatedWith(Names.named(env.getKey())).toInstance(
                        Integer.parseInt(env.getValue()));
            } else {
                bind(String.class).annotatedWith(Names.named(env.getKey())).toInstance(env.getValue());
            }
        });

        // This binds instances of a particular object that are Annotated

        bind(ProPrint.class).toInstance(new ProPrint());

        runDriverBindings();

        // Anything that has implemented the Initializer.class will have the
        // init() be called after everything is injected.
        bindListener(Matchers.any(), new TypeListener() {
            @Override
            public <I> void hear(final TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register((InjectionListener<I>) i -> {
                    if (i instanceof Initializer) {
                        Initializer m = (Initializer) i;
                        m.init();
                    }
                });
            }
        });
    }

    public void runDriverBindings(){}

    /**
     * If a Key is not set, it will give it a default
     * @param key
     * @param value
     */
    private void setDefault(String key, String value) {
        if (envMap.get(key) == null || StringUtils.isBlank(envMap.get(key))) {
            ProPrint.debug("Setting Default value. "+key+"="+value);
            envMap.put(key, value);
        }
    }

    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

}
