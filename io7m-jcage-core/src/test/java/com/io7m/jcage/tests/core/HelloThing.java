package com.io7m.jcage.tests.core;

public final class HelloThing implements Runnable
{
  @Override public void run()
  {
    final Class<? extends HelloThing> c = this.getClass();
    final ClassLoader cl = c.getClassLoader();
    System.out.println("Hello from " + c + ", loaded by " + cl);
  }
}
