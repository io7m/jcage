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

import java.security.Permission;

/**
 * Permissions required to administer the sandbox system.
 */

public final class JCSandboxAdminPermission extends Permission
{
  private static final long serialVersionUID = 1L;
  private final String name;
  private final String actions;

  /**
   * Construct a permission.
   *
   * @param in_name    The permission name
   * @param in_actions The permitted actions
   */

  public JCSandboxAdminPermission(
    final String in_name,
    final String in_actions)
  {
    super(in_name);
    this.name = NullCheck.notNull(in_name);
    this.actions = NullCheck.notNull(in_actions);
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.actions.hashCode();
    result = (prime * result) + this.name.hashCode();
    return result;
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final JCSandboxAdminPermission other = (JCSandboxAdminPermission) obj;
    return this.actions.equals(other.actions) && this.name.equals(other.name);
  }

  @Override public boolean implies(
    final @Nullable Permission in_p)
  {
    final Permission p = NullCheck.notNull(in_p);
    if (p instanceof JCSandboxAdminPermission) {
      final JCSandboxAdminPermission jp = (JCSandboxAdminPermission) p;

      final String this_name = this.getName();
      final String this_act = this.getActions();
      final String other_name = jp.getName();
      final String other_act = jp.getActions();

      if ("*".equals(this_name)) {
        if ("*".equals(this_act)) {
          return true;
        }
        return this_act.equals(other_act);
      }

      if (this_name.equals(other_name)) {
        if ("*".equals(this_act)) {
          return true;
        }
        return this_act.equals(other_act);
      }
    }

    return false;
  }

  @Override public String getActions()
  {
    return this.actions;
  }
}
