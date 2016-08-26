/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.Factory;

/**
 *
 */
@FunctionalInterface
public interface FactoryCreator {
  <T> Factory<T> factoryOf(String className);
}
