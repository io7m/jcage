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
import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * <p>
 * A sandboxing class loader.
 * </p>
 * <p>
 * The loader takes a delegate class loader <tt>dc</tt>, a delegate class
 * loader policy <tt>dcp</tt>, a class loader policy <tt>cp</tt>, a URL
 * <tt>u</tt>, and a class name resolver <tt>r</tt>.
 * </p>
 * <p>
 * When a request is made to load a class with name <tt>c</tt>, the loader
 * checks <tt>cp</tt> to determine whether or not it is allowed to attempt to
 * load the class itself. If loading is allowed, it loads the class by asking
 * <tt>r</tt> to return bytes for <tt>c</tt>. If <tt>r</tt> returns class
 * bytes, the loader returns a new class with a {@link ProtectionDomain}
 * domain using a {@link CodeSource} containing the location <tt>u</tt>. If
 * <tt>r</tt> does not return any class bytes, the loader instead checks
 * <tt>dcp</tt> to see if it is allowed access to the class <tt>c</tt> via
 * <tt>dc</tt>. If access is allowed and <tt>dc</tt> was actually provided,
 * the loader calls <tt>dc</tt> to load the class. If access is not permitted,
 * the loader raises {@link SecurityException}. If <tt>dc</tt> was not
 * provided, the loader raises {@link ClassNotFoundException}.
 * </p>
 * <p>
 * The results of the above procedure are <i>cached</i> iff the loader loaded
 * the class (it does not cache the results if <tt>dc</tt> loads the class).
 * </p>
 * <p>
 * The loader loads classes from the bytes returned by the given
 * {@link JCClassNameResolverType} resolver.
 * </p>
 */

@SuppressWarnings("synthetic-access") public final class JCClassLoader extends
  ClassLoader
{
  private final Map<String, Class<?>>   cache;
  private final CodeSource              code_source;
  private final @Nullable ClassLoader   delegate;
  private final JCClassLoaderPolicyType delegate_policy;
  private final JCClassLoaderPolicyType policy;
  private final JCClassNameResolverType resolver;

  JCClassLoader(
    final @Nullable ClassLoader in_delegate,
    final JCClassLoaderPolicyType in_delegate_policy,
    final JCClassNameResolverType in_resolver,
    final JCClassLoaderPolicyType in_policy,
    final URL in_url)
  {
    super(null);
    this.delegate = in_delegate;
    this.resolver = NullCheck.notNull(in_resolver);
    this.delegate_policy = NullCheck.notNull(in_delegate_policy);
    this.policy = NullCheck.notNull(in_policy);
    this.cache = new ConcurrentHashMap<String, Class<?>>();
    this.code_source =
      new CodeSource(NullCheck.notNull(in_url), (Certificate[]) null);
  }

  @Override protected Class<?> findClass(
    final @Nullable String in_name)
    throws ClassNotFoundException
  {
    final String name = NullCheck.notNull(in_name);

    if (this.cache.containsKey(name)) {
      return NullCheck.notNull(this.cache.get(name));
    }

    if (this.policy.policyAllowsClass(name)) {
      final byte[] cb = this.resolver.resolveToBytes(name);
      if (cb != null) {
        final ProtectionDomain pd =
          new ProtectionDomain(
            this.code_source,
            new Permissions(),
            this,
            null);
        final Class<?> rc =
          NullCheck.notNull(this.defineClass(name, cb, 0, cb.length, pd));
        this.cache.put(name, rc);
        return rc;
      }
    }

    if (this.delegate_policy.policyAllowsClass(name)) {
      final ClassLoader d = this.delegate;
      if (d != null) {
        return NullCheck.notNull(d.loadClass(name));
      }
    } else {
      throw new SecurityException("Class access denied: " + name);
    }

    throw new ClassNotFoundException(name);
  }

  private Class<?> load(
    final boolean resolve,
    final String name)
    throws ClassNotFoundException
  {
    final Class<?> c = this.findClass(name);
    if (resolve) {
      this.resolveClass(c);
    }
    return NullCheck.notNull(c);
  }

  @Override protected Class<?> loadClass(
    final @Nullable String in_name,
    final boolean resolve)
    throws ClassNotFoundException
  {
    final String name = NullCheck.notNull(in_name);

    try {
      final Class<?> r =
        AccessController
          .doPrivileged(new PrivilegedExceptionAction<Class<?>>() {
            @Override public Class<?> run()
              throws ClassNotFoundException
            {
              return JCClassLoader.this.load(resolve, name);
            }
          });
      return NullCheck.notNull(r);
    } catch (final PrivilegedActionException e) {
      final Throwable cause = e.getCause();
      if (cause instanceof ClassNotFoundException) {
        throw (ClassNotFoundException) cause;
      }
      if (cause instanceof SecurityException) {
        throw (SecurityException) cause;
      }
      throw new UnreachableCodeException(cause);
    }
  }

  @Override public String toString()
  {
    final StringBuilder b = new StringBuilder();
    b.append("[JCClassLoader ");
    b.append(this.code_source.getLocation());
    b.append("]");
    return NullCheck.notNull(b.toString());
  }
}
