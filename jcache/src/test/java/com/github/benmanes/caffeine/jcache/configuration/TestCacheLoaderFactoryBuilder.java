/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import java.util.Map;

import javax.cache.configuration.Factory;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;

/**
 *
 */
public class TestCacheLoaderFactoryBuilder
    implements TypesafeConfigurator.CacheLoaderFactoryBuilder<Integer, Integer> {

  @Override
  public Factory<? extends CacheLoader<Integer, Integer>> getFactory(String className) {
    return new TestCacheLoaderFactory();
  }

  public static class TestCacheLoaderFactory implements Factory<CacheLoader<Integer, Integer>> {
    @Override
    public CacheLoader<Integer, Integer> create() {
      return new FakeCacheLoader();
    }
  }
  
  public static class FakeCacheLoader implements CacheLoader<Integer, Integer> {

    @Override
    public Integer load(Integer key) throws CacheLoaderException {
      return null;
    }

    @Override
    public Map<Integer, Integer> loadAll(Iterable<? extends Integer> keys)
        throws CacheLoaderException {
      return null;
    }
    
  }
}
