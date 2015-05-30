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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.io7m.jcage.core.JCRuleConclusion;
import com.io7m.jcage.core.JCSequentialPolicy;
import com.io7m.jcage.core.JCSequentialPolicyBuilderType;

@SuppressWarnings("static-method") public final class JCSequentialPolicyTest
{
  @Test public void testPolicyEmpty_0()
  {
    final JCSequentialPolicyBuilderType jpb =
      JCSequentialPolicy.newPolicyBuilder(
        JCRuleConclusion.DENY,
        JCRuleConclusion.DENY);
    final JCSequentialPolicy jp = jpb.build();
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Object"));
    Assert.assertFalse(jp.policyAllowsResource("file.txt"));
  }

  @Test public void testPolicy_0()
  {
    final JCSequentialPolicyBuilderType jpb =
      JCSequentialPolicy.newPolicyBuilder(
        JCRuleConclusion.DENY,
        JCRuleConclusion.DENY);

    jpb.addClassRule(
      Pattern.compile("java.lang.Object"),
      JCRuleConclusion.ALLOW,
      true);

    final JCSequentialPolicy jp = jpb.build();
    Assert.assertTrue(jp.policyAllowsClass("java.lang.Object"));
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Object.More"));
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Nothing"));
    Assert.assertFalse(jp.policyAllowsResource("file.txt"));
  }

  @Test public void testPolicy_1()
  {
    final JCSequentialPolicyBuilderType jpb =
      JCSequentialPolicy.newPolicyBuilder(
        JCRuleConclusion.DENY,
        JCRuleConclusion.DENY);

    jpb.addClassRule(
      Pattern.compile("java.lang.Object"),
      JCRuleConclusion.ALLOW,
      false);
    jpb.addClassRule(
      Pattern.compile("java.lang.Object.More"),
      JCRuleConclusion.ALLOW,
      false);

    final JCSequentialPolicy jp = jpb.build();
    Assert.assertTrue(jp.policyAllowsClass("java.lang.Object"));
    Assert.assertTrue(jp.policyAllowsClass("java.lang.Object.More"));
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Nothing"));
    Assert.assertFalse(jp.policyAllowsResource("file.txt"));
  }

  @Test public void testPolicy_2()
  {
    final JCSequentialPolicyBuilderType jpb =
      JCSequentialPolicy.newPolicyBuilder(
        JCRuleConclusion.DENY,
        JCRuleConclusion.DENY);

    jpb.addClassRule(
      Pattern.compile("java.lang.Object"),
      JCRuleConclusion.ALLOW,
      false);
    jpb.addClassRule(
      Pattern.compile("java.lang.Object"),
      JCRuleConclusion.DENY,
      false);

    final JCSequentialPolicy jp = jpb.build();
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Object"));
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Object.More"));
    Assert.assertFalse(jp.policyAllowsClass("java.lang.Nothing"));
    Assert.assertFalse(jp.policyAllowsResource("file.txt"));
  }
}
