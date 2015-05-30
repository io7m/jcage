/*
 * Copyright © 2015 <code@io7m.com> http://io7m.com
 * 
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jcage.tests.core.sandbox_breaker;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Policy;

import com.io7m.jcage.core.JCClassLoaderPolicyUnrestricted;
import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCSandboxes;
import com.io7m.jcage.core.JCSandboxesType;
import com.io7m.jcage.tests.core.DangerousGetClassLoader;
import com.io7m.jcage.tests.core.SandboxBreakerType;
import com.io7m.jcage.tests.core.ThingWithPrivate;
import com.io7m.jnull.NullCheck;

public final class SandboxBreaker implements SandboxBreakerType
{
  public SandboxBreaker()
  {

  }

  @Override public void tryCheckPermission(
    final Permission p)
    throws Exception
  {
    System.out.println("tryCheckPermission: starting");
    final SecurityManager sm = NullCheck.notNull(System.getSecurityManager());
    System.out.println("tryCheckPermission: checking");
    sm.checkPermission(p);
  }

  @Override public void tryClassLoaderCreate()
    throws Exception
  {
    System.out.println("tryClassLoaderCreate: starting");

    final ClassLoader c = new ClassLoader(null) {
    };
    c.loadClass("java.lang.Object");
  }

  @Override public void tryClassLoaderGet()
    throws Exception
  {
    System.out.println("tryClassLoaderGet: starting");
    ClassLoader.getSystemClassLoader();
  }

  @Override public void tryClassLoaderGetSystem()
    throws Exception
  {
    System.out.println("tryClassLoaderGetSystem: starting");
    final ClassLoader sys = ClassLoader.getSystemClassLoader();
    System.out.println("tryClassLoaderGetSystem: " + sys);
  }

  @Override public void tryFileWrite()
    throws Exception
  {
    System.out.println("tryFileWrite: starting");
    final File f = new File("test.txt");
    try (final FileOutputStream fs = new FileOutputStream(f)) {
      fs.write("FAILURE".getBytes());
      fs.flush();
    }
  }

  @Override public void tryGetPolicy()
    throws Exception
  {
    System.out.println("tryGetPolicy: starting");
    Policy.getPolicy();
  }

  @Override public void tryLoadNative()
    throws Exception
  {
    System.out.println("tryLoadNative: starting");
    System.loadLibrary("libc");
  }

  @Override public void tryReflectionGetDeclared()
    throws Exception
  {
    System.out.println("tryReflectionGetDeclared: starting");
    final ThingWithPrivate t = new ThingWithPrivate();
    final Class<? extends ThingWithPrivate> c = t.getClass();
    final Field f = c.getDeclaredField("value");
    f.setInt(t, 23);
  }

  @Override public void trySandboxesCreate()
    throws Exception
  {
    System.out.println("trySandboxesCreate: starting");

    JCSandboxesType s = null;
    try {
      s = JCSandboxes.get();
    } catch (final AccessControlException x) {
      throw new AssertionError(x);
    }

    s.createSandbox(
      "a",
      DangerousGetClassLoader.get(),
      JCClassLoaderPolicyUnrestricted.get(),
      JCClassNameResolverClasspath.get(),
      JCClassLoaderPolicyUnrestricted.get(),
      new Permissions());
  }

  @Override public void trySandboxesGet()
    throws Exception
  {
    System.out.println("trySandboxesGet: starting");
    JCSandboxes.get();
  }

  @Override public void trySetPolicy()
    throws Exception
  {
    System.out.println("trySetPolicy: starting");
    Policy.setPolicy(new Policy() {
    });
  }

  @Override public void tryThreadCreate()
    throws Exception
  {
    System.out.println("tryThreadCreate: starting");
    final Thread t = new Thread(new Runnable() {
      @Override public void run()
      {
        System.out.println("tryThreadCreate: Hello");
      }
    });
    t.start();
  }

  @Override public void tryThreadGroupCreate()
    throws Exception
  {
    System.out.println("tryThreadGroupCreate: starting");
    final Thread t = Thread.currentThread();
    System.out.println("tryThreadGroupCreate: " + t);
    final ThreadGroup tg = t.getThreadGroup();
    System.out.println("tryThreadGroupCreate: " + tg);
    tg.destroy();
    System.out.println("tryThreadGroupCreate: unexpectedly succeeded");
  }
}
