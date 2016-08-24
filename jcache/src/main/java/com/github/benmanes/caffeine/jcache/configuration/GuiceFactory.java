/**
 * 
 */
package com.github.benmanes.caffeine.jcache.configuration;

import javax.cache.configuration.Factory;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 *
 */
@SuppressWarnings("serial")
public class GuiceFactory<T> implements Factory<T> {
    private static final Logger LOG = LoggerFactory.getLogger(GuiceFactory.class);
    
    @Inject
    private static Injector injector;

    private final String className;
    
    private GuiceFactory(String className) {
        this.className = className;
    }
    
    public static <T> Factory<T> of(String className) {
        return new GuiceFactory<T>(className);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T create() {
        if (injector == null) {
            throw new RuntimeException("Injector is null");
        }
        
        try {
            return (T) injector.getInstance(Class.forName(className));
        } catch (ClassNotFoundException e) {
            LOG.error("Unable to instantiate: {}", className, e);
            throw new RuntimeException("Unable to find class: " + className);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object other) {
      if (this == other) return true;
      if (other == null || getClass() != other.getClass()) return false;

      GuiceFactory that = (GuiceFactory) other;

      if (!className.equals(that.className)) return false;

      return true;
    }

    @Override
    public int hashCode() {
      return className.hashCode();
    }
}
