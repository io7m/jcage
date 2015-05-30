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

import java.io.FilePermission;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.Permissions;
import java.security.PrivilegedAction;

import com.io7m.jcage.core.JCClassLoaderPolicyType;
import com.io7m.jcage.core.JCClassLoaderPolicyUnrestricted;
import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCSandboxType;
import com.io7m.jcage.core.JCSandboxes;
import com.io7m.jcage.core.JCSandboxesType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

public final class SandboxerMain
{
  public static void main(
    final String args[])
    throws Exception
  {
    final JCSandboxesType ss = JCSandboxes.get();

    final String sandboxed_name = Sandboxed.class.getCanonicalName();

    final JCClassLoaderPolicyType sandbox_class_policy =
      new JCClassLoaderPolicyType() {
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

    final Permissions p = new Permissions();

    final JCSandboxType s =
      ss.createSandbox(
        "example",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        sandbox_class_policy,
        p);

    final Class<Runnable> c =
      (Class<Runnable>) s.getSandboxLoadedClass(sandboxed_name);
    final Constructor<Runnable> cc =
      c.getConstructor(SandboxListenerType.class);

    final SandboxListenerType listener = new SandboxListenerType() {
      @Override public void onMessageReceived(
        final String m)
      {
        System.out.println("Listener: received: " + m);

        AccessController.doPrivileged(new PrivilegedAction<Void>() {
          @Override public @Nullable Void run()
          {
            System.out
              .println("Listener: Attempting to perform privileged operation");
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
