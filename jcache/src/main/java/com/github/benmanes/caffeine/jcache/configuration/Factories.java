/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.Factory;

/**
 *
 */
public class Factories<T> {

  public static <T> Factory<T> getFactory(String className, String loaderFactory) {
    switch (loaderFactory) {
      case "guice":
        return GuiceFactory.of(className);
      default:
        throw new RuntimeException(
            "Unable to create a Factory for: " + loaderFactory + " for className: " + className);
    }
  }
}
