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

import java.net.URL;
import java.security.Permissions;

import org.junit.Assert;
import org.junit.Test;
import org.valid4j.exceptions.RequireViolation;

import com.io7m.jcage.core.JCClassLoader;
import com.io7m.jcage.core.JCClassLoaderPolicyType;
import com.io7m.jcage.core.JCClassLoaderPolicyUnrestricted;
import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCSandboxClassMissingException;
import com.io7m.jcage.core.JCSandboxClassWrongLoaderException;
import com.io7m.jcage.core.JCSandboxDuplicateException;
import com.io7m.jcage.core.JCSandboxSecurityException;
import com.io7m.jcage.core.JCSandboxType;
import com.io7m.jcage.core.JCSandboxes;
import com.io7m.jcage.core.JCSandboxesType;

@SuppressWarnings({ "null", "static-method", "unchecked" }) public final class JCSandboxesTest
{
  @Test public void testCreateSandbox()
    throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testCreateSandbox",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        JCClassLoaderPolicyUnrestricted.get(),
        new Permissions());

    final URL s0u = s0.getSandboxURL();
    Assert.assertEquals(
      "http://jcage-sandbox/testCreateSandbox",
      s0u.toString());
    Assert.assertEquals("testCreateSandbox", s0.getSandboxName());
  }

  @Test(expected = RequireViolation.class) public
    void
    testCreateSandboxBadName()
      throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();

    sb0.createSandbox(
      "",
      ClassLoader.getSystemClassLoader(),
      JCClassLoaderPolicyUnrestricted.get(),
      JCClassNameResolverClasspath.get(),
      JCClassLoaderPolicyUnrestricted.get(),
      new Permissions());
  }

  @Test(expected = JCSandboxDuplicateException.class) public
    void
    testCreateSandboxDuplicate()
      throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    sb0.createSandbox(
      "testCreateSandboxDuplicate",
      ClassLoader.getSystemClassLoader(),
      JCClassLoaderPolicyUnrestricted.get(),
      JCClassNameResolverClasspath.get(),
      JCClassLoaderPolicyUnrestricted.get(),
      new Permissions());
    sb0.createSandbox(
      "testCreateSandboxDuplicate",
      ClassLoader.getSystemClassLoader(),
      JCClassLoaderPolicyUnrestricted.get(),
      JCClassNameResolverClasspath.get(),
      JCClassLoaderPolicyUnrestricted.get(),
      new Permissions());
  }

  @Test public void testEmpty0()
  {
    final JCSandboxesType b = JCSandboxes.get();
  }

  @Test public void testEmpty1()
  {
    final JCSandboxesType b0 = JCSandboxes.get();
    final JCSandboxesType b1 = JCSandboxes.get();
    Assert.assertSame(b0, b1);
  }

  @Test public void testSandboxClass_0()
    throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testSandboxClass_0",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        JCClassLoaderPolicyUnrestricted.get(),
        new Permissions());

    final Class<Thing> c0 =
      (Class<Thing>) s0
        .getSandboxLoadedClass("com.io7m.jcage.tests.core.Thing");

    final JCClassLoader l0 = s0.getSandboxClassLoader();
    Assert.assertSame(c0.getClassLoader(), l0);

    final Class<Thing> c1 =
      (Class<Thing>) s0
        .getSandboxLoadedClass("com.io7m.jcage.tests.core.Thing");

    Assert.assertSame(c0, c1);
  }

  @Test public void testSandboxClass_1()
    throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testSandboxClass_1",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        JCClassLoaderPolicyUnrestricted.get(),
        new Permissions());

    final Class<Runnable> c0 =
      (Class<Runnable>) s0
        .getSandboxLoadedClass("com.io7m.jcage.tests.core.HelloThing");

    final Runnable i = c0.newInstance();
    i.run();
  }

  @Test(expected = JCSandboxClassMissingException.class) public
    void
    testSandboxClassMissing_0()
      throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testSandboxClassMissing_0",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        JCClassLoaderPolicyUnrestricted.get(),
        new Permissions());

    s0.getSandboxLoadedClass("absolutely.does.not.exist.Thing");
  }

  @Test(expected = JCSandboxClassWrongLoaderException.class) public
    void
    testSandboxClassWrong_0()
      throws Exception
  {
    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testSandboxClassWrong_0",
        ClassLoader.getSystemClassLoader(),
        JCClassLoaderPolicyUnrestricted.get(),
        JCClassNameResolverClasspath.get(),
        JCClassLoaderPolicyUnrestricted.get(),
        new Permissions());

    s0.getSandboxLoadedClass("java.lang.Object");
  }

  @Test(expected = JCSandboxSecurityException.class) public
    void
    testSandboxClassDenied_0()
      throws Exception
  {
    final JCClassLoaderPolicyType deny = new JCClassLoaderPolicyType() {
      @Override public boolean policyAllowsResource(
        final String name)
      {
        return false;
      }

      @Override public boolean policyAllowsClass(
        final String name)
      {
        return false;
      }
    };

    final JCSandboxesType sb0 = JCSandboxes.get();
    final JCSandboxType s0 =
      sb0.createSandbox(
        "testSandboxClassDenied_0",
        ClassLoader.getSystemClassLoader(),
        deny,
        JCClassNameResolverClasspath.get(),
        deny,
        new Permissions());

    s0.getSandboxLoadedClass("com.io7m.jcage.tests.core.Thing");
  }
}
