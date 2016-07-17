package org.apache.zeppelin.beam;

import java.lang.reflect.InvocationTargetException;

import org.apache.beam.examples.MinimalWordCount;

/**
 * 
 * @author admin
 *
 */
public class Compiler {

  public static Class<?> compile(String className, String code) throws Exception {
    return InMemoryJavaCompiler.compile(className, code);
  }

  public static void main(String[] args) {
    StringBuffer sourceCode = new StringBuffer();
    // sourceCode.append("package com.novelari.staticrepl;\n");
    // sourceCode.append("public class HelloClass {\n");
    // sourceCode.append("public static String hello() { return \"hello\"; }\n");
    // sourceCode.append("public static void main(String[] args){\n"
    // + "System.out.println(\"Hellow World\");\n" + "}");
    // sourceCode.append("}");

    String base = "src/main/java/org/apache/beam/examples";
    String class1name = MinimalWordCount.class.getCanonicalName();
    String class1Code = FileUtil.readUTF8(base + "/MinimalWordCount.java");
    // System.out.println(class1name);
    // System.out.println(class1Code);

    try {
      // Class<?> helloClass = Compiler.compile(class1name, class1Code);
      Class<?> helloClass = Compiler.compile("org.apache.beam.examples.MinimalWordCount",
          sourceCode.toString());
      if (helloClass != null) {
        invokeMain(helloClass);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void invokeMain(Class<?> clazz) {

    try {
      // Class.forName("HelloWorld")
      clazz.getDeclaredMethod("main", new Class[] { String[].class }).invoke(null,
          new Object[] { null });
      // Object x = clazz.getDeclaredMethod("hello", new Class[] {}).invoke(null, new Object[] {
      // String.class });
      // Object x = clazz.getDeclaredMethod("hello", null).invoke(null, null);
      // System.out.println(x);
    } catch (NoSuchMethodException e) {
      System.err.println("No such method: " + e);
    } catch (IllegalAccessException e) {
      System.err.println("Illegal access: " + e);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }

  }
}
