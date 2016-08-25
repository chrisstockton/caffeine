/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import java.util.Collection;

import javax.cache.Cache.Entry;
import javax.cache.configuration.Factory;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;

/**
 * 
 * @author z0018v4
 *
 */
public class TestCacheWriterFactoryBuilder
    implements TypesafeConfigurator.CacheWriterFactoryBuilder<Integer, Integer> {

  @Override
  public Factory<? extends CacheWriter<? super Integer, ? super Integer>> getFactory(
      String className) {
    return new TestCacheWriterFactory();
  }

  public static class TestCacheWriterFactory implements Factory<CacheWriter<Integer, Integer>> {
    @Override
    public CacheWriter<Integer, Integer> create() {
      return new FakeCacheWriter();
    }
  }
  
  public static class FakeCacheWriter implements CacheWriter<Integer, Integer> {
    @Override
    public void write(Entry<? extends Integer, ? extends Integer> entry)
        throws CacheWriterException {
    }

    @Override
    public void writeAll(Collection<Entry<? extends Integer, ? extends Integer>> entries)
        throws CacheWriterException {
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void deleteAll(Collection<?> keys) throws CacheWriterException {
      // TODO Auto-generated method stub
      
    }
    
  }
}
