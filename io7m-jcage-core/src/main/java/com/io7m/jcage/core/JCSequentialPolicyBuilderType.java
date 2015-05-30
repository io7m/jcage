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

import java.util.regex.Pattern;

/**
 * A sequential policy builder.
 */

public interface JCSequentialPolicyBuilderType
{
  /**
   * Add a rule at the end of the current list of rules.
   *
   * @param p
   *          The pattern against which class names will be matched
   * @param c
   *          The conclusion of the rule
   * @param quick
   *          <tt>true</tt> if the rule is quick; processing stops when this
   *          rule matches
   */

  void addClassRule(
    Pattern p,
    JCRuleConclusion c,
    boolean quick);

  /**
   * Add a rule at the end of the current list of rules.
   *
   * @param p
   *          The pattern against which resource names will be matched
   * @param c
   *          The conclusion of the rule
   * @param quick
   *          <tt>true</tt> if the rule is quick; processing stops when this
   *          rule matches
   */

  void addResourceRule(
    Pattern p,
    JCRuleConclusion c,
    boolean quick);

  /**
   * @return A policy based on the parameters given so far
   */

  JCSequentialPolicy build();
}
