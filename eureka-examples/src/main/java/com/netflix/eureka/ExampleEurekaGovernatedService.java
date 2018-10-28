package com.netflix.eureka;

import com.google.inject.AbstractModule;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.MyDataCenterInstanceConfig;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.guice.EurekaModule;
import com.netflix.governator.InjectorBuilder;
import com.netflix.governator.LifecycleInjector;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Sample Eureka service that registers with Eureka to receive and process requests, using EurekaModule.
 */
public class ExampleEurekaGovernatedService {

    static class ExampleServiceModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(ExampleServiceBase.class).asEagerSingleton();
        }
    }

    /**
     * This will be read by server internal discovery client. We need to salience it.
     */
    private static void injectEurekaConfiguration() throws UnknownHostException {
        String myHostName = InetAddress.getLocalHost().getHostName();
        String myServiceUrl = "http://" + myHostName + ":8080/v2/";

        System.setProperty("eureka.region", "default");
        System.setProperty("eureka.name", "eureka");
        System.setProperty("eureka.vipAddress", "eureka.mydomain.net");
        System.setProperty("eureka.port", "8080");
        System.setProperty("eureka.preferSameZone", "false");
        System.setProperty("eureka.shouldUseDns", "false");
        System.setProperty("eureka.shouldFetchRegistry", "false");
        System.setProperty("eureka.serviceUrl.defaultZone", myServiceUrl);
        System.setProperty("eureka.serviceUrl.default.defaultZone", myServiceUrl);
        System.setProperty("eureka.awsAccessId", "fake_aws_access_id");
        System.setProperty("eureka.awsSecretKey", "fake_aws_secret_key");
        System.setProperty("eureka.numberRegistrySyncRetries", "0");
    }

    private static LifecycleInjector init() throws Exception {
        injectEurekaConfiguration();
        System.out.println("Creating injector for Example Service");

        LifecycleInjector injector = InjectorBuilder
                .fromModules(new EurekaModule(), new ExampleServiceModule())
                .overrideWith(new AbstractModule() {
                    @Override
                    protected void configure() {
                        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
                        bind(DynamicPropertyFactory.class).toInstance(configInstance);
                        // the default impl of EurekaInstanceConfig is CloudInstanceConfig, which we only want in an AWS
                        // environment. Here we override that by binding MyDataCenterInstanceConfig to EurekaInstanceConfig.
                        bind(EurekaInstanceConfig.class).to(MyDataCenterInstanceConfig.class);

                        // (DiscoveryClient optional bindings) bind the optional event bus
                        // bind(EventBus.class).to(EventBusImpl.class).in(Scopes.SINGLETON);
                    }
                })
                .createInjector();

        System.out.println("Done creating the injector");
        return injector;
    }

    public static void main(String[] args) throws Exception {
        LifecycleInjector injector = null;
        try {
            injector = init();
            injector.awaitTermination();
        } catch (Exception e) {
            System.out.println("Error starting the sample service: " + e);
            e.printStackTrace();
        } finally {
            if (injector != null) {
                injector.shutdown();
            }
        }
    }

}
