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
import org.valid4j.Assertive;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p> Functions to validate sandbox names. </p> <p> The format of sandbox names
 * are restricted in order to ensure that they can always be used safely inside
 * {@link java.net.URL} values. </p>
 */

public final class JCSandboxNames
{
  private static final Pattern PATTERN;
  private static final String  PATTERN_TEXT;

  static {
    PATTERN_TEXT = "[[\\p{Alnum}][_]]{1,}";
    PATTERN = NullCheck.notNull(
      Pattern.compile(
        JCSandboxNames.PATTERN_TEXT, Pattern.UNICODE_CHARACTER_CLASS));
  }

  private JCSandboxNames()
  {
    throw new UnreachableCodeException();
  }

  /**
   * Check if <tt>name</tt> is valid with respect to {@link
   * #nameIsValid(String)}.
   *
   * @param name The name
   *
   * @return The name
   */

  public static String nameCheck(
    final String name)
  {
    NullCheck.notNull(name);
    Assertive.require(
      JCSandboxNames.nameIsValid(name),
      "Sandbox name '%s' does not match the pattern '%s'",
      name,
      JCSandboxNames.PATTERN_TEXT);
    return name;
  }

  /**
   * @param name The requested sandbox name
   *
   * @return <tt>true</tt> is a valid sandbox name
   */

  public static boolean nameIsValid(
    final String name)
  {
    NullCheck.notNull(name);

    final Matcher matcher = JCSandboxNames.PATTERN.matcher(name);
    return matcher.matches();
  }
}
