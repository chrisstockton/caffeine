/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.Factory;
import javax.cache.configuration.FactoryBuilder;

/**
 *
 */
public class TestFactoryCreator implements FactoryCreator {

  @Override
  public <T> Factory<T> factoryOf(String className) {
    return FactoryBuilder.factoryOf(className);
  }
  
  static class TestFactory<T> implements Factory<T> {
    @Override
    public T create() {
      return null;
    }
  }
}
