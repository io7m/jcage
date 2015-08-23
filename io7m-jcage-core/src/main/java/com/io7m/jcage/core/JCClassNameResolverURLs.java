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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * A class resolver that can load class data from a list of URLs.
 */

public final class JCClassNameResolverURLs implements JCClassNameResolverType
{
  private final List<URL> urls;

  private JCClassNameResolverURLs(
    final List<URL> in_urls)
  {
    this.urls = NullCheck.notNullAll(in_urls, "URLs");
  }

  /**
   * @param urls A list of URLs
   *
   * @return A class resolver that can load class data from the given URLs.
   */

  public static JCClassNameResolverType get(
    final List<URL> urls)
  {
    return new JCClassNameResolverURLs(urls);
  }

  private static @Nullable byte[] tryDirectory(
    final File dir,
    final String path)
  {
    final File actual = new File(dir, path);

    final ByteArrayOutputStream bao = new ByteArrayOutputStream(4096);
    try (final InputStream stream = new FileInputStream(actual)) {
      final byte[] buffer = new byte[4096];
      while (true) {
        final int r = stream.read(buffer);
        if (r == -1) {
          break;
        }
        bao.write(buffer, 0, r);
      }
      return bao.toByteArray();
    } catch (final FileNotFoundException e) {
      // e.printStackTrace();
    } catch (final IOException e) {
      // e.printStackTrace();
    }

    return null;
  }

  private static @Nullable byte[] tryJar(
    final String path,
    final URL u)
  {
    try {
      final String s = NullCheck.notNull(String.format("jar:%s!/%s", u, path));
      final URL ju = new URL(s);
      final JarURLConnection c = (JarURLConnection) ju.openConnection();

      final ByteArrayOutputStream bao = new ByteArrayOutputStream(4096);
      try (final InputStream stream = c.getInputStream()) {
        final byte[] buffer = new byte[4096];
        while (true) {
          final int r = stream.read(buffer);
          if (r == -1) {
            break;
          }
          bao.write(buffer, 0, r);
        }
      }

      return bao.toByteArray();
    } catch (final MalformedURLException e) {
      // e.printStackTrace();
    } catch (final IOException e) {
      // e.printStackTrace();
    }

    return null;
  }

  @Override public @Nullable byte[] resolveToBytes(
    final String name)
  {
    final String path = NullCheck.notNull(name.replace('.', '/') + ".class");

    for (final URL in_u : this.urls) {
      final URL u = NullCheck.notNull(in_u);

      if ("file".equals(u.getProtocol())) {
        final File dir = new File(u.getPath());
        if (dir.isDirectory()) {
          {
            final byte[] bytes =
              JCClassNameResolverURLs.tryDirectory(dir, path);
            if (bytes != null) {
              return bytes;
            }
          }
        }
      }

      {
        final byte[] bytes = JCClassNameResolverURLs.tryJar(path, u);
        if (bytes != null) {
          return bytes;
        }
      }
    }

    return null;
  }
}
