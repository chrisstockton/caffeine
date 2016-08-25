/**
 * 
 */
package test;

import static java.util.stream.Collectors.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheResolverFactory;
import javax.cache.configuration.Factory;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import javax.cache.spi.CachingProvider;

import org.jsr107.ri.annotations.DefaultCacheResolverFactory;
import org.jsr107.ri.annotations.guice.module.CacheAnnotationsModule;
import org.testng.annotations.Test;

import com.github.benmanes.caffeine.jcache.configuration.TypesafeConfigurator;
import com.github.benmanes.caffeine.jcache.spi.CaffeineCachingProvider;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * @author z0018v4
 *
 */
public class GuiceFactoryBuilderTest {

  @Test
  public void testGuiceCacheLoaderFactoryBuilder() {
    System.setProperty("config.resource", "/read-through-test.conf");
    Module module = Modules.override(new CacheAnnotationsModule()).with(new CaffeineJCacheModule());
    Injector injector = Guice.createInjector(module, new AbstractModule() {
      @Override
      protected void configure() {
        Map<Integer, Person> persons =
            Arrays.asList(new Person(1, "Dick"), new Person(2, "Jane"), new Person(3, "Spot"))
                .stream().collect(toMap(Person::getId, Function.identity()));
        bind(PersonCacheLoader.class);
        bind(PersonDao.class).toInstance(new PersonDao(persons));
      }
    });
    
    GuiceCacheLoaderFactoryBuilder builder = new GuiceCacheLoaderFactoryBuilder<>(injector);
    TypesafeConfigurator.setCacheLoaderFactoryBuilder(builder);
    
    CacheManager manager = injector.getInstance(CacheManager.class);
    Cache<Integer, Person> cache = manager.getCache("read-through");
    
    Person p = cache.get(1);
    assertThat(p, is(notNullValue()));
    assertThat(p.getName(), is("Dick"));
    
    Map<Integer, Person> persons = cache.getAll(Sets.newHashSet(2, 3));
    assertThat(persons.size(), is(2));
    assertThat(persons.get(2).getName(), is("Jane"));
    assertThat(persons.get(3).getName(), is("Spot"));
  }

  static class Person {
    final Integer id;
    final String name;

    public Person(Integer id, String name) {
      super();
      this.id = id;
      this.name = name;
    }

    public Integer getId() {
      return id;
    }

    public String getName() {
      return name;
    }
  }

  static class PersonCacheLoader implements CacheLoader<Integer, Person> {
    private final PersonDao dao;

    @Inject
    public PersonCacheLoader(PersonDao dao) {
      super();
      this.dao = dao;
    }

    @Override
    public Person load(Integer key) throws CacheLoaderException {
      return dao.get(key);
    }

    @Override
    public Map<Integer, Person> loadAll(Iterable<? extends Integer> keys)
        throws CacheLoaderException {
      return dao.getAll(Sets.newHashSet(keys)).stream()
          .collect(toMap(Person::getId, Function.identity()));
    }

  }

  static class PersonDao {
    final Map<Integer, Person> persons;

    public PersonDao(Map<Integer, Person> persons) {
      super();
      this.persons = persons;
    }

    public Person get(Integer id) {
      return persons.get(id);
    }

    public Collection<Person> getAll(Collection<Integer> ids) {
      List<Person> result = new ArrayList<>(ids.size());
      ids.forEach(id -> {
        Person p = persons.get(id);
        if (p != null)
          result.add(p);
      });
      return result;
    }
  }
  
  static class GuiceCacheLoaderFactoryBuilder<K, V> implements TypesafeConfigurator.CacheLoaderFactoryBuilder<K, V> {
    private final Injector injector;
    
    public GuiceCacheLoaderFactoryBuilder(Injector injector) {
      super();
      this.injector = injector;
    }

    @Override
    public Factory<? extends CacheLoader<K, V>> getFactory(String className) {
      return new GuiceFactory(injector, className);
    }
  }
  
  static class GuiceFactory<T> implements Factory<T> {
    private final Injector injector;
    private final String className;
    
    public GuiceFactory(Injector injector, String className) {
      this.injector = injector;
      this.className = className;
    }

    @Override
    public T create() {
      try {
        return (T) injector.getInstance(Class.forName(className));
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }
  
  static final class CaffeineJCacheModule extends AbstractModule {
    @Override protected void configure() {
      CachingProvider provider = Caching.getCachingProvider(
          CaffeineCachingProvider.class.getName());
      CacheManager cacheManager = provider.getCacheManager(
          provider.getDefaultURI(), provider.getDefaultClassLoader());
      bind(CacheManager.class).toInstance(cacheManager);
      bind(CacheResolverFactory.class).toInstance(new DefaultCacheResolverFactory(cacheManager));
    }
  }
}
