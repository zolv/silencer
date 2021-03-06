package com.greyturtle.silencer.di;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import io.javalin.Javalin;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Context {

  private Javalin serverInstance;

  private static final Context singleton = new Context();

  public static Context get() {
    /* Eager initialization. No need for double-checked locking */
    return singleton;
  }

  private final Map<String, Supplier<?>> beans = Collections.synchronizedMap(new HashMap<>());

  private final AtomicBoolean withBalance = new AtomicBoolean(false);

  public void registerSingleton(final String name, final Class<?> clazz) {
    this.registerSingleton(name, createInstance(name, clazz));
  }

  public void registerSingleton(final String name, final Object instance) {
    this.beans.put(name, () -> instance);
  }

  public void register(final String name, final Class<?> clazz) {
    this.beans.put(
        name,
        () -> {
          return createInstance(name, clazz);
        });
  }

  private Object createInstance(final String name, final Class<?> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException
        | java.lang.InstantiationException e) {
      log.error(e.getMessage(), e);
      throw new InstantiationException(name, e);
    }
  }
  
  public <T> T getInstance(final Class<T> clazz) throws InstantiationException {
    return getInstance(clazz.getSimpleName());
  }

  public <T> T getInstance(final String name) throws InstantiationException {
    return (T)
        Optional.ofNullable(this.beans.get(name))
            .orElseThrow(() -> new UnknownBeanException(name))
            .get();
  }

  public AtomicBoolean getWithBalance() {
    return this.withBalance;
  }

  public void setServerInstance(final Javalin server) {
    this.serverInstance = server;
  }

  public Javalin getServerInstance() {
    return this.serverInstance;
  }
}
