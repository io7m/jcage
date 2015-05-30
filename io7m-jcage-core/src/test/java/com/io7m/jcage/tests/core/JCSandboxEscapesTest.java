package com.io7m.jcage.tests.core;

import java.io.FilePermission;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.Permissions;
import java.security.SecurityPermission;
import java.util.PropertyPermission;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.io7m.jcage.core.JCClassLoaderPolicyType;
import com.io7m.jcage.core.JCClassLoaderPolicyUnrestricted;
import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCSandboxAdminPermission;
import com.io7m.jcage.core.JCSandboxType;
import com.io7m.jcage.core.JCSandboxes;
import com.io7m.jcage.core.JCSandboxesType;

@SuppressWarnings({ "null", "static-method", "unchecked" }) public final class JCSandboxEscapesTest
{
  private static String accessDeniedMessage(
    final Permission p)
  {
    final StringBuilder s = new StringBuilder();
    s.append("access denied ");
    s.append(p);
    return s.toString();
  }

  @Rule public final ExpectedException expected = ExpectedException.none();

  private SandboxBreakerType getSandboxBreaker(
    final String box_name)
    throws AssertionError
  {
    return this.getSandboxBreakerWith(box_name, new Permissions());
  }

  private SandboxBreakerType getSandboxBreakerWith(
    final String box_name,
    final Permissions perms)
    throws AssertionError
  {
    try {
      final JCClassLoaderPolicyType sandbox_policy =
        new JCClassLoaderPolicyType() {
          @Override public boolean policyAllowsClass(
            final String name)
          {
            return name
              .startsWith("com.io7m.jcage.tests.core.sandbox_breaker.");
          }

          @Override public boolean policyAllowsResource(
            final String name)
          {
            return false;
          }
        };

      final JCSandboxesType sb0 = JCSandboxes.get();
      final JCSandboxType s0 =
        sb0.createSandbox(
          box_name,
          ClassLoader.getSystemClassLoader(),
          JCClassLoaderPolicyUnrestricted.get(),
          JCClassNameResolverClasspath.get(),
          sandbox_policy,
          perms);

      final Class<SandboxBreakerType> c0 =
        (Class<SandboxBreakerType>) s0
          .getSandboxLoadedClass("com.io7m.jcage.tests.core.sandbox_breaker.SandboxBreaker");

      return c0.newInstance();
    } catch (final Exception e) {
      throw new AssertionError(e);
    }
  }

  @Test public void testCheckPermission()
    throws Exception
  {
    final FilePermission p = new FilePermission("/tmp/z", "read");
    final Permissions ps = new Permissions();
    ps.add(p);
    final SandboxBreakerType i =
      this.getSandboxBreakerWith("tryCheckPermission", ps);
    i.tryCheckPermission(p);
  }

  @Test public void testCheckPermissionCreateSandbox()
    throws Exception
  {
    final JCSandboxAdminPermission p =
      new JCSandboxAdminPermission("*", "createSandbox");
    final Permissions ps = new Permissions();
    ps.add(p);
    final SandboxBreakerType i =
      this.getSandboxBreakerWith("tryCheckPermissionCreateSandbox", ps);
    i.tryCheckPermission(p);
  }

  @Test public void testLoadNative()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("loadLibrary.libc")));

    final SandboxBreakerType i = this.getSandboxBreaker("tryLoadNative");
    i.tryLoadNative();
  }

  @Test public void testReflectionGetDeclared()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("accessDeclaredMembers")));

    final SandboxBreakerType i =
      this.getSandboxBreaker("tryReflectionGetDeclared");
    i.tryReflectionGetDeclared();
  }

  @Test public void testClassLoaderCreate()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("createClassLoader")));

    final SandboxBreakerType i =
      this.getSandboxBreaker("tryClassLoaderCreate");
    i.tryClassLoaderCreate();
  }

  @Test public void testClassLoaderGet()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("getClassLoader")));

    final SandboxBreakerType i = this.getSandboxBreaker("tryClassLoaderGet");
    i.tryClassLoaderGet();
  }

  @Test public void testClassLoaderGetSystem()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("getClassLoader")));

    final SandboxBreakerType i =
      this.getSandboxBreaker("tryClassLoaderGetSystem");
    i.tryClassLoaderGetSystem();
  }

  @Test public void testFileWrite()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new FilePermission("test.txt", "write")));

    final SandboxBreakerType i = this.getSandboxBreaker("tryFileWrite");
    i.tryFileWrite();
  }

  @Test public void testGetPolicy()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new SecurityPermission("getPolicy")));

    final SandboxBreakerType i = this.getSandboxBreaker("tryGetPolicy");
    i.tryGetPolicy();
  }

  @Test public void testSandboxesCreate()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected
      .expectMessage(JCSandboxEscapesTest
        .accessDeniedMessage(new JCSandboxAdminPermission(
          "a",
          "createSandbox")));

    final Permissions perms = new Permissions();
    perms.add(new JCSandboxAdminPermission("*", "getSandboxes"));
    perms.add(new SecurityPermission("getPolicy"));
    perms.add(new PropertyPermission("java.class.path", "read"));
    perms.add(new PropertyPermission("path.separator", "read"));

    final SandboxBreakerType i =
      this.getSandboxBreakerWith("testSandboxesCreate", perms);
    i.trySandboxesCreate();
  }

  @Test public void testSandboxesGet()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected
      .expectMessage(JCSandboxEscapesTest
        .accessDeniedMessage(new JCSandboxAdminPermission("*", "getSandboxes")));

    final SandboxBreakerType i = this.getSandboxBreaker("testSandboxesGet");
    i.trySandboxesGet();
  }

  @Test public void testSetPolicy()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new SecurityPermission("setPolicy")));

    final SandboxBreakerType i = this.getSandboxBreaker("trySetPolicy");
    i.trySetPolicy();
  }

  @Test public void testThreadCreate()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("modifyThreadGroup")));

    final SandboxBreakerType i = this.getSandboxBreaker("tryThreadCreate");
    i.tryThreadCreate();
  }

  @Test public void testThreadGroupCreate()
    throws Exception
  {
    this.expected.expect(AccessControlException.class);
    this.expected.expectMessage(JCSandboxEscapesTest
      .accessDeniedMessage(new RuntimePermission("modifyThreadGroup")));

    final SandboxBreakerType i =
      this.getSandboxBreaker("tryThreadGroupCreate");
    i.tryThreadGroupCreate();
  }
}
