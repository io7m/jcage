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
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jcage.core.JCClassNameResolverClasspath;
import com.io7m.jcage.core.JCClassNameResolverType;

@SuppressWarnings("static-method") public final class JCClassNameResolverClasspathTest
{
  @Test public void testInvalidURL()
  {
    final List<URL> r =
      JCClassNameResolverClasspath.getClasspathElements("in val id:", ":");
    Assert.assertTrue(r.isEmpty());
  }

  @Test public void testNonexistent()
  {
    final JCClassNameResolverType r = JCClassNameResolverClasspath.get();
    Assert.assertNull(r.resolveToBytes("nonexistent"));
  }

  @Test public void testInaccessible_0()
  {
    final JCClassNameResolverType r = JCClassNameResolverClasspath.get();
    Assert.assertNull(r.resolveToBytes("java.lang.Object"));
  }

  @Test public void testOK_0()
  {
    final JCClassNameResolverType r = JCClassNameResolverClasspath.get();
    Assert.assertNotNull(r
      .resolveToBytes("com.io7m.junreachable.UnreachableCodeException"));
  }
}
