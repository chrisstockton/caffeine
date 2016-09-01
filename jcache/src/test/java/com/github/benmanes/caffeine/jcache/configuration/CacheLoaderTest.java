/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.Map;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.spi.CachingProvider;

import org.jsr107.ri.annotations.DefaultCacheResolverFactory;
import org.jsr107.ri.annotations.guice.module.CacheAnnotationsModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * @author z0018v4
 *
 */
public class CacheLoaderTest {
  @Inject
  CacheManager cacheManager;
  
  @BeforeMethod
  public void beforeMethod() {
    Module module = Modules.override(new CacheAnnotationsModule()).with(new CaffeineJCacheModule());
    Guice.createInjector(module).injectMembers(this);
  }
  
  @Test
  public void loadNullValue() {
    Cache<Object, Object> cache = cacheManager.getCache("load-null-values");
    assertThat("Got a null value", cache.get(new Object()), is(nullValue()));
  }
  
  @Test
  public void loadAll() {
    Cache<Object, Object> cache = cacheManager.getCache("load-null-values");
    assertThat("Empty result", cache.getAll(Sets.newHashSet("a", "b", "c")).isEmpty());
  }
  
  public static final class Loader implements CacheLoader<Object, Object> {
    @Override
    public Object load(Object key) throws CacheLoaderException {
      return null;
    }

    @Override
    public Map<Object, Object> loadAll(Iterable<? extends Object> keys)
        throws CacheLoaderException {
      return null;
    }
    
  }
  
  static final class CaffeineJCacheModule extends AbstractModule {
    @Override protected void configure() {
      configureCachingProvider();
    }

    /** Resolves the annotations to the provider as multiple are on the IDE's classpath. */
    void configureCachingProvider() {
      CachingProvider provider = Caching.getCachingProvider(
          CaffeineCachingProvider.class.getName());
      CacheManager cacheManager = provider.getCacheManager(
          provider.getDefaultURI(), provider.getDefaultClassLoader());
      bind(CacheResolverFactory.class).toInstance(new DefaultCacheResolverFactory(cacheManager));
      bind(CacheManager.class).toInstance(cacheManager);
    }
  }
}
