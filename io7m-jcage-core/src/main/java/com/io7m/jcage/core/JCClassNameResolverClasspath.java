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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A class resolver that can load class data from the current classpath.
 */

public final class JCClassNameResolverClasspath implements
  JCClassNameResolverType
{
  public static JCClassNameResolverType get()
  {
    final String path =
      NullCheck.notNull(System.getProperty("java.class.path"));
    return JCClassNameResolverClasspath.getWithPath(path);
  }

  public static JCClassNameResolverType getWithPath(
    final String path)
  {
    NullCheck.notNull(path);
    final String sep =
      NullCheck.notNull(System.getProperty("path.separator"));
    return JCClassNameResolverClasspath.getWithPathAndSeparator(path, sep);
  }

  public static JCClassNameResolverType getWithPathAndSeparator(
    final String path,
    final String sep)
  {
    NullCheck.notNull(path);
    NullCheck.notNull(sep);

    final List<URL> urls =
      JCClassNameResolverClasspath.getClasspathElements(path, sep);

    return new JCClassNameResolverClasspath(JCClassNameResolverURLs.get(urls));
  }

  /**
   * Split <tt>path</tt> into elements on <tt>sep</tt>.
   *
   * @param path
   *          The path
   * @param sep
   *          The separator
   * @return The URL elements
   */

  public static List<URL> getClasspathElements(
    final String path,
    final String sep)
  {
    NullCheck.notNull(path);
    NullCheck.notNull(sep);

    final String[] segments = path.split(sep);
    final List<URL> urls = new ArrayList<>();
    for (int index = 0; index < segments.length; ++index) {
      try {
        URI uri = new URI(segments[index]);
        if (null == uri.getScheme()) {
          uri = new URI("file", null, uri.getPath(), null);
        }
        urls.add(uri.toURL());
      } catch (final MalformedURLException e) {
        // Nothing
      } catch (final URISyntaxException e) {
        // Nothing
      }
    }
    return urls;
  }

  private final JCClassNameResolverType actual;

  private JCClassNameResolverClasspath(
    final JCClassNameResolverType in_actual)
  {
    this.actual = NullCheck.notNull(in_actual);
  }

  @Override public @Nullable byte[] resolveToBytes(
    final String name)
  {
    return this.actual.resolveToBytes(name);
  }
}
