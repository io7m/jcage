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

package com.io7m.jcage.tests.core;

import java.security.Permission;

/**
 * Operations that try to subvert the sandbox.
 */

public interface SandboxBreakerType
{
  void tryCheckPermission(
    Permission p)
    throws Exception;

  void tryClassLoaderCreate()
    throws Exception;

  void tryClassLoaderGet()
    throws Exception;

  void tryClassLoaderGetSystem()
    throws Exception;

  void tryFileWrite()
    throws Exception;

  void tryGetPolicy()
    throws Exception;

  void tryLoadNative()
    throws Exception;

  void trySandboxesCreate()
    throws Exception;

  void trySandboxesGet()
    throws Exception;

  void trySetPolicy()
    throws Exception;

  void tryThreadCreate()
    throws Exception;

  void tryThreadGroupCreate()
    throws Exception;

  void tryReflectionGetDeclared()
    throws Exception;
}
