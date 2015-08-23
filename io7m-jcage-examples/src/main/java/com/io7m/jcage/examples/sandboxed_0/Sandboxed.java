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

package com.io7m.jcage.examples.sandboxed_0;

import com.io7m.junreachable.UnreachableCodeException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Some sandboxed code.
 */

public final class Sandboxed implements Runnable
{
  /**
   * Primary constructor.
   */

  public Sandboxed()
  {

  }

  @Override public void run()
  {
    System.out.println("Sandboxed: running sandboxed");

    try {
      System.out.println("Sandboxed: attempting to open file.txt (should "
                         + "fail)");
      new FileOutputStream("file.txt");
    } catch (final SecurityException e) {
      System.out.println(
        "Sandboxed: failed to open file.txt as expected: " + e.getMessage());
    } catch (final FileNotFoundException e) {
      throw new UnreachableCodeException(e);
    }

    System.out.println("Sandboxed: finished");
  }
}
