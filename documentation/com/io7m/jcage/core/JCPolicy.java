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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import org.valid4j.Assertive;

import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.Provider;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> The main sandboxing {@link Policy} implementation. </p> <p> The policy
 * maps sandbox URLs to sets of {@link Permission} values. </p>
 */

public final class JCPolicy extends Policy
{
  private final Map<URL, Permissions> sandboxes;
  private       Permissions           permissions_default;

  JCPolicy()
  {
    this.sandboxes = new ConcurrentHashMap<URL, Permissions>();
    final Permissions q = new Permissions();
    q.add(new AllPermission());
    q.setReadOnly();
    this.permissions_default = q;
  }

  @Override public @Nullable Provider getProvider()
  {
    return null;
  }

  @Override public void refresh()
  {
    // Nothing
  }

  @Override public PermissionCollection getPermissions(
    final @Nullable CodeSource codesource)
  {
    throw new UnsupportedOperationException();
  }

  @Override public PermissionCollection getPermissions(
    final @Nullable ProtectionDomain domain)
  {
    throw new UnsupportedOperationException();
  }

  @Override public @Nullable String getType()
  {
    return null;
  }

  @Override public @Nullable Parameters getParameters()
  {
    return null;
  }

  @Override public boolean implies(
    final @Nullable ProtectionDomain in_domain,
    final @Nullable Permission in_permission)
  {
    final ProtectionDomain domain = NullCheck.notNull(in_domain);
    final Permission permission = NullCheck.notNull(in_permission);
    final CodeSource source = domain.getCodeSource();

    final Permissions box_perms = this.sandboxes.get(source.getLocation());
    if (box_perms != null) {
      return box_perms.implies(permission);
    }

    return this.permissions_default.implies(permission);
  }

  /**
   * Set the default (non-sandboxed) permissions.
   *
   * @param p The permissions
   */

  public void setDefaultPermissions(
    final Permissions p)
  {
    NullCheck.notNull(p);

    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      final JCSandboxAdminPermission rp =
        new JCSandboxAdminPermission("*", "setDefaultPermissions");
      sm.checkPermission(rp);
    }

    final Permissions q = new Permissions();
    final Enumeration<Permission> pe = p.elements();
    while (pe.hasMoreElements()) {
      q.add(pe.nextElement());
    }
    q.setReadOnly();
    this.permissions_default = q;
  }

  /**
   * Set the permissions for the given sandbox.
   *
   * @param name The sandbox name
   * @param p    The permissions
   */

  public void putSandboxPermissions(
    final URL name,
    final Permissions p)
  {
    NullCheck.notNull(name);
    NullCheck.notNull(p);

    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      final JCSandboxAdminPermission rp =
        new JCSandboxAdminPermission(name.toString(), "setSandboxPermissions");
      sm.checkPermission(rp);
    }

    Assertive.require(
      this.sandboxes.containsKey(name) == false,
      "Sandbox does not already exist with URL '%s'",
      name);

    this.sandboxes.put(name, p);
  }
}
