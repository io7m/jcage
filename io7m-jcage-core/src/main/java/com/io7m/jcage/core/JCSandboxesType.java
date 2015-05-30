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

import java.security.Permissions;

/**
 * The main interface allowing the creation and configuration of sandboxes.
 */

public interface JCSandboxesType
{
  /**
   * <p>
   * Create a new sandbox.
   * </p>
   * 
   * @param name
   *          The name of the sandbox (must be valid, see @link
   *          {@link JCSandboxNames}).
   * @param sandbox_host_classloader
   *          The class loader used to load classes on the host (outside of
   *          the sandbox)
   * @param sandbox_host_class_policy
   *          The policy that controls access to classes outside of the
   *          sandbox
   * @param class_resolver
   *          The class name resolver
   * @param sandbox_class_policy
   *          The policy that controls which classes will be loaded using the
   *          sandbox class loader
   * @param p
   *          The permissions assigned to the sandbox
   * @return A new sandbox
   * @throws JCSandboxDuplicateException
   *           If the sandbox already exists
   * @throws JCSandboxException
   *           On other sandbox errors
   */

  JCSandboxType createSandbox(
    String name,
    ClassLoader sandbox_host_classloader,
    JCClassLoaderPolicyType sandbox_host_class_policy,
    JCClassNameResolverType class_resolver,
    JCClassLoaderPolicyType sandbox_class_policy,
    Permissions p)
    throws JCSandboxException;
}
