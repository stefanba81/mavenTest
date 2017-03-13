package com.stefan;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.stefan.modules.TestingModule;
import com.stefan.utilities.Initializer;
import com.stefan.utilities.ProPrint;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import stefan.TestConfiguration;

/**
 * Created by StefanB on 3/7/2017.
 */
public class BaseTest implements Initializer {

    @Inject
    public TestConfiguration tConfig;
    @Inject
    public ProPrint proPrint;
    protected Injector injector;
    public TestingModule module;


    @BeforeClass(groups="driverSetup")
    public void setupTestConfiguration(ITestContext context){
        if(module == null) {
            System.out.println("Injecting TestingModule into " + this.getClass().getName());
            injector = Guice.createInjector(new TestingModule(context));
        } else {
            System.out.println("Injecting " + module.toString() + " into "+this.getClass().getName());
            module.setContextParams(context.getCurrentXmlTest().getAllParameters());
            injector = Guice.createInjector(module);
        }
        injector.injectMembers(this);
        //context.setAttribute("api_version", yConfig.getApiVersion());
    }

    /**
     * This runs post-injection
     */
    @Override
    public void init() {
        if (tConfig.getLogResults()) {
            ProPrint.info(String.format("%s will log results to TestRail using %s.",this.getClass().getName(), tConfig.getTestPlanId()));
            //testCaseHandler.createTestCasesMap();
        }
    }

}
