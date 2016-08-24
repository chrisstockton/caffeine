/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

/**
 *
 */
public class Factories<T> {

    public static <T> Factory<T> getFactory(String className, String loaderFactory) {
        switch (loaderFactory) {
        case "guice":
            return GuiceFactory.of(className);
        default:
            return FactoryBuilder.factoryOf(className);
        }
    }
}
