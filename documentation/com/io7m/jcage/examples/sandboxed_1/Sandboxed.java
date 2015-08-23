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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

import java.io.FilePermission;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Some sandboxed code.
 */

public final class Sandboxed implements Runnable
{
  private final SandboxListenerType listener;

  /**
   * Construct the sandboxed code.
   *
   * @param in_listener A listener that can return results to the sandbox
   */

  public Sandboxed(
    final SandboxListenerType in_listener)
  {
    this.listener = NullCheck.notNull(in_listener);
  }

  /**
   * A privileged operation.
   */

  private static void tryPrivileged()
  {
    System.out.println("Sandboxed: Attempting to perform privileged operation");

    try {
      final SecurityManager sm = NullCheck.notNull(System.getSecurityManager());
      sm.checkPermission(new FilePermission("/tmp/z", "write"));
    } catch (final SecurityException e) {
      System.out.println(
        "Sandboxed: Correctly failed to perform privileged operation: "
        + e.getMessage());
    }
  }

  @Override public void run()
  {
    System.out.println("Sandboxed: running sandboxed");
    System.out.println("Sandboxed: calling listener");
    this.listener.onMessageReceived("Hello");
    System.out.println("Sandboxed: called listener");

    /**
     * Try to perform a privileged operation. This will obviously fail.
     */

    AccessController.doPrivileged(
      new PrivilegedAction<Void>()
      {
        @Override public @Nullable Void run()
        {
          Sandboxed.tryPrivileged();
          return null;
        }
      });

    System.out.println("Sandboxed: finished");
  }
}
