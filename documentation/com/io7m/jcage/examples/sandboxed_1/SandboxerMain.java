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

package com.io7m.jcage.examples.sandboxed_1;

import com.io7m.jcage.core.JCClassLoaderPolicyType;
import com.io7m.jcage.core.JCClassLoaderPolicyUnrestricted;
import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCClassNameResolverType;
import com.io7m.jcage.core.JCSandboxType;
import com.io7m.jcage.core.JCSandboxes;
import com.io7m.jcage.core.JCSandboxesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

import java.io.FilePermission;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;

/**
 * An example program that starts various sandboxes and demonstrates the
 * isolation of the code within.
 */

public final class SandboxerMain
{
  private SandboxerMain()
  {

  }

  /**
   * Main program.
   *
   * @param args Command line arguments
   *
   * @throws Exception On errors
   */

  public static void main(
    final String[] args)
    throws Exception
  {
    /**
     * Retrieve access to the sandbox administration interface. This
     * registers a security manager if required, and also performs an
     * access check to prevent sandboxed code from trying to get access
     * to the interface.
     */

    final JCSandboxesType ss = JCSandboxes.get();

    /**
     * Get access to the system classloader and create a policy that indicates
     * that it is allowed to load any classes it wants.
     */

    final ClassLoader host_classloader = ClassLoader.getSystemClassLoader();
    final JCClassLoaderPolicyType host_classloader_policy =
      JCClassLoaderPolicyUnrestricted.get();

    /**
     * Create a class resolver that can load classes from the classpath
     * on behalf of the sandbox.
     */

    final JCClassNameResolverType sandbox_class_resolver =
      JCClassNameResolverClasspath.get();

    /**
     * Create a policy that denies the sandbox from loading any classes
     * except for the single Sandboxed class.
     */

    final String sandboxed_name = Sandboxed.class.getCanonicalName();
    final JCClassLoaderPolicyType sandbox_class_policy =
      new JCClassLoaderPolicyType()
      {
        @Override public boolean policyAllowsResource(
          final String name)
        {
          return false;
        }

        @Override public boolean policyAllowsClass(
          final String name)
        {
          return name.startsWith(sandboxed_name);
        }
      };

    /**
     * Create a new sandbox called "example" that has no permissions.
     */

    final JCSandboxType s = ss.createSandbox(
      "example",
      host_classloader,
      host_classloader_policy,
      sandbox_class_resolver,
      sandbox_class_policy,
      new Permissions());

    /**
     * Get access to a sandbox-loaded class. Instantiate it, and execute it.
     */

    @SuppressWarnings("unchecked") final Class<Runnable> c =
      (Class<Runnable>) s.getSandboxLoadedClass(sandboxed_name);
    final Constructor<Runnable> cc =
      c.getConstructor(SandboxListenerType.class);

    /**
     * A function evaluated by sandboxed code that demonstrates how
     * sandboxed code can be granted temporary privileges with
     * `AccessController.doPrivileged`.
     */

    final SandboxListenerType listener = new SandboxListenerType()
    {
      @Override public void onMessageReceived(
        final String m)
      {
        System.out.println("Listener: received: " + m);

        AccessController.doPrivileged(
          new PrivilegedAction<Void>()
          {
            @Override public @Nullable Void run()
            {
              System.out.println(
                "Listener: Attempting to perform privileged operation");
              final SecurityManager sm =
                NullCheck.notNull(System.getSecurityManager());
              sm.checkPermission(new FilePermission("/tmp/z", "write"));
              System.out.println("Listener: Performed privileged operation");
              return null;
            }
          });
      }
    };

    final Runnable r = cc.newInstance(listener);
    System.out.println("Running sandboxed code...");
    r.run();
    System.out.println("Finished running sandboxed code.");
  }
}
