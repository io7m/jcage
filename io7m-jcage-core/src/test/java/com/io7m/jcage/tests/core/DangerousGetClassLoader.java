package com.io7m.jcage.tests.core;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.io7m.jnull.NullCheck;

public final class DangerousGetClassLoader
{
  public static ClassLoader get()
  {
    return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
      @Override public ClassLoader run()
      {
        return NullCheck.notNull(ClassLoader.getSystemClassLoader());
      }
    });
  }
}
