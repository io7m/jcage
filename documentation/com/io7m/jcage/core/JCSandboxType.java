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

package com.io7m.jcage.core;

import java.net.URL;
import java.security.Permissions;

/**
 * The type of sandboxes.
 */

public interface JCSandboxType
{
  /**
   * @return The sandbox's class loader
   */

  JCClassLoader getSandboxClassLoader();

  /**
   * Return a class that is guaranteed to have been loaded by the sandbox class
   * loader.
   *
   * @param name The name of the class
   *
   * @return A loaded class
   *
   * @throws JCSandboxClassWrongLoaderException If the given class was loaded by
   *                                            a class loader other than that
   *                                            created for the sandbox
   * @throws JCSandboxClassMissingException     If the given class does not
   *                                            exist
   * @throws JCSandboxException                 On other errors
   */

  Class<?> getSandboxLoadedClass(
    String name)
    throws
    JCSandboxException,
    JCSandboxClassWrongLoaderException,
    JCSandboxClassMissingException;

  /**
   * @return The short name of the sandbox
   */

  String getSandboxName();

  /**
   * @return The permissions assigned to the sandbox
   */

  Permissions getSandboxPermissions();

  /**
   * Get the identifying URL of the sandbox. This URL uniquely identifies the
   * sandbox for the purposes of assigning permissions in Java {@link
   * java.security.Policy} implementations.
   *
   * @return The identifying URL of the sandbox
   */

  URL getSandboxURL();
}
