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

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jcage.core.JCSandboxAdminPermission;

@SuppressWarnings("static-method") public final class JCSandboxAdminPermissionTest
{
  @Test public void testEqualsHashCode()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p2 =
      new JCSandboxAdminPermission("z", "x");
    final JCSandboxAdminPermission p3 =
      new JCSandboxAdminPermission("x", "z");

    Assert.assertEquals(p0, p0);
    Assert.assertEquals(p0, p1);
    Assert.assertEquals(p1, p0);
    Assert.assertNotEquals(p0, null);
    Assert.assertNotEquals(p0, Integer.valueOf(23));
    Assert.assertNotEquals(p0, p2);
    Assert.assertNotEquals(p0, p3);
    Assert.assertNotEquals(p2, p3);

    Assert.assertEquals(p0.hashCode(), p0.hashCode());
    Assert.assertEquals(p0.hashCode(), p1.hashCode());
    Assert.assertEquals(p1.hashCode(), p0.hashCode());
    Assert.assertNotEquals(p0.hashCode(), p2.hashCode());
    Assert.assertNotEquals(p0.hashCode(), p3.hashCode());
    Assert.assertNotEquals(p2.hashCode(), p3.hashCode());

    System.out.println(p0.toString());

    Assert.assertEquals(p0.toString(), p0.toString());
    Assert.assertEquals(p0.toString(), p1.toString());
    Assert.assertEquals(p1.toString(), p0.toString());
    Assert.assertNotEquals(p0.toString(), p2.toString());
    Assert.assertNotEquals(p0.toString(), p3.toString());
    Assert.assertNotEquals(p2.toString(), p3.toString());
  }

  @Test public void testImpliesFalse_0()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "*");
    final RuntimePermission p1 = new RuntimePermission("*");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_1()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("*", "*");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_2()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("*", "*");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_3()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("*", "*");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_4()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "x");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_5()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("x", "z");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesFalse_6()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("x", "x");
    Assert.assertFalse(p0.implies(p1));
  }

  @Test public void testImpliesTrue_0()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("*", "*");
    Assert.assertTrue(p0.implies(p1));
  }

  @Test public void testImpliesTrue_1()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "*");
    Assert.assertTrue(p0.implies(p1));
  }

  @Test public void testImpliesTrue_2()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("*", "z");
    Assert.assertTrue(p0.implies(p1));
  }

  @Test public void testImpliesTrue_3()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("*", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "z");
    Assert.assertTrue(p0.implies(p1));
  }

  @Test public void testImpliesTrue_4()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "*");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "*");
    Assert.assertTrue(p0.implies(p1));
  }

  @Test public void testImpliesTrue_5()
  {
    final JCSandboxAdminPermission p0 =
      new JCSandboxAdminPermission("z", "z");
    final JCSandboxAdminPermission p1 =
      new JCSandboxAdminPermission("z", "z");
    Assert.assertTrue(p0.implies(p1));
  }
}
