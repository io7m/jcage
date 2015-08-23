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
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valid4j.Assertive;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.Permissions;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation of the {@link JCSandboxesType} interface.
 */

public final class JCSandboxes implements JCSandboxesType
{
  private static final Logger            LOG;
  private static final JCPolicy          POLICY;
  private static final JCSandboxesType   SANDBOXES;
  private static final JCSecurityManager SECURITY_MANAGER;

  static {
    SECURITY_MANAGER = new JCSecurityManager();
    POLICY = new JCPolicy();
    LOG = NullCheck.notNull(LoggerFactory.getLogger(JCSandboxes.class));
    SANDBOXES = new JCSandboxes();
  }

  private final Map<String, JCSandboxType> sandboxes;

  private JCSandboxes()
  {
    this.sandboxes = new HashMap<String, JCSandboxType>();
  }

  /**
   * @return A reference to the current sandbox interface
   */

  public static JCSandboxesType get()
  {
    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(new JCSandboxAdminPermission("*", "getSandboxes"));
    }

    JCSandboxes.installSecurityManagerAndPolicy();
    return JCSandboxes.SANDBOXES;
  }

  private static void installSecurityManagerAndPolicy()
  {
    final Policy p = Policy.getPolicy();
    if ((p instanceof JCPolicy) == false) {
      JCSandboxes.LOG.debug("installing policy");
      Policy.setPolicy(JCSandboxes.POLICY);
    }

    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      Assertive.require(
        sm instanceof JCSecurityManager,
        "Installed security manager must be of type %s",
        JCSecurityManager.class);
    } else {
      JCSandboxes.LOG.debug("installing security manager");
      System.setSecurityManager(JCSandboxes.SECURITY_MANAGER);
    }
  }

  /**
   * Set the default global permissions.
   *
   * @param p The set of permissions
   */

  public static void setGlobalPermissions(
    final Permissions p)
  {
    JCSandboxes.POLICY.setDefaultPermissions(p);
  }

  @Override public JCSandboxType createSandbox(
    final String name,
    final ClassLoader sandbox_host_classloader,
    final JCClassLoaderPolicyType sandbox_host_class_policy,
    final JCClassNameResolverType in_class_resolver,
    final JCClassLoaderPolicyType sandbox_class_policy,
    final Permissions in_p)
    throws JCSandboxException
  {
    NullCheck.notNull(name);
    NullCheck.notNull(sandbox_host_classloader);
    NullCheck.notNull(sandbox_host_class_policy);
    NullCheck.notNull(in_class_resolver);
    NullCheck.notNull(sandbox_class_policy);
    NullCheck.notNull(in_p);

    JCSandboxNames.nameCheck(name);

    final SecurityManager sm = System.getSecurityManager();
    if (sm != null) {
      sm.checkPermission(new JCSandboxAdminPermission(name, "createSandbox"));
    }

    synchronized (this.sandboxes) {
      if (this.sandboxes.containsKey(name)) {
        throw new JCSandboxDuplicateException(name);
      }

      final Sandbox s = new Sandbox(
        name,
        sandbox_host_classloader,
        sandbox_host_class_policy,
        in_class_resolver,
        sandbox_class_policy,
        in_p);

      JCSandboxes.POLICY.putSandboxPermissions(s.getSandboxURL(), in_p);
      this.sandboxes.put(name, s);
      return s;
    }
  }

  private static final class Sandbox implements JCSandboxType
  {
    private final JCClassLoader           class_loader;
    private final JCClassNameResolverType class_resolver;
    private final ClassLoader             host_class_loader;
    private final JCClassLoaderPolicyType host_class_policy;
    private final String                  name;
    private final Permissions             permissions;
    private final URI                     uri;
    private final URL                     url;

    public Sandbox(
      final String in_name,
      final ClassLoader in_sandbox_host_classloader,
      final JCClassLoaderPolicyType in_sandbox_host_class_policy,
      final JCClassNameResolverType in_class_resolver,
      final JCClassLoaderPolicyType in_sandbox_class_policy,
      final Permissions in_p)
    {
      try {
        this.name = NullCheck.notNull(in_name);
        this.host_class_loader = NullCheck.notNull(in_sandbox_host_classloader);
        this.host_class_policy =
          NullCheck.notNull(in_sandbox_host_class_policy);
        this.class_resolver = NullCheck.notNull(in_class_resolver);
        this.uri =
          NullCheck.notNull(URI.create("http://jcage-sandbox/" + in_name));
        this.url = NullCheck.notNull(this.uri.toURL());
        this.permissions = NullCheck.notNull(in_p);
        this.class_loader = new JCClassLoader(
          this.host_class_loader,
          this.host_class_policy,
          this.class_resolver,
          in_sandbox_class_policy,
          this.url);
      } catch (final MalformedURLException e) {
        throw new UnreachableCodeException(e);
      }
    }

    @Override public JCClassLoader getSandboxClassLoader()
    {
      return this.class_loader;
    }

    @Override public Class<?> getSandboxLoadedClass(
      final String in_class_name)
      throws JCSandboxException
    {
      try {
        final String class_name = NullCheck.notNull(in_class_name);
        final Class<?> c = this.class_loader.loadClass(class_name);

        final ClassLoader loader = c.getClassLoader();
        if (loader != this.class_loader) {
          final StringBuilder sb = new StringBuilder();
          sb.append("Expected a class loaded by the sandbox loader.\n");
          sb.append("  Class:    ");
          sb.append(c);
          sb.append("\n");
          sb.append("  Expected: ");
          sb.append(this.class_loader);
          sb.append("\n");
          sb.append("  Got:      ");
          sb.append(loader);
          sb.append("\n");
          final String m = NullCheck.notNull(sb.toString());
          throw new JCSandboxClassWrongLoaderException(m);
        }

        return c;
      } catch (final ClassNotFoundException e) {
        throw new JCSandboxClassMissingException(e);
      } catch (final SecurityException e) {
        throw new JCSandboxSecurityException(e);
      }
    }

    @Override public String getSandboxName()
    {
      return this.name;
    }

    @Override public Permissions getSandboxPermissions()
    {
      return this.permissions;
    }

    @Override public URL getSandboxURL()
    {
      return this.url;
    }
  }
}
