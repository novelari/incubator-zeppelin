package org.apache.beam.examples;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 
 * @author admin
 *
 */

public class JarOutputStreamDemo {
  public static void main(String[] args) throws IOException {
    // prepare Manifest file
    String version = "1.0.0";
    String author = "concretepage";
    Manifest manifest = new Manifest();
    Attributes global = manifest.getMainAttributes();
    global.put(Attributes.Name.MANIFEST_VERSION, version);
    global.put(new Attributes.Name("Created-By"), author);

    // create required jar name
    String jarFileName = "cp.jar";
    JarOutputStream jos = null;
    try {
      File jarFile = new File(jarFileName);
      OutputStream os = new FileOutputStream(jarFile);
      jos = new JarOutputStream(os, manifest);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Collect all file and class names to iterate
    List<String> fileList = new ArrayList<String>();
    String rootLocation = "D:/cp/jar/testjar/";
    fileList.add("javax/swing/BoxBeanInfo.class");
    fileList.add("javax/swing/text/JTextComponentBeanInfo.class");

    // start writing in jar
    int len = 0;
    byte[] buffer = new byte[1024];
    for (String file : fileList) {
      // create JarEntry
      JarEntry je = new JarEntry(file);
      je.setComment("Craeting Jar");
      je.setTime(Calendar.getInstance().getTimeInMillis());
      System.out.println(je);
      jos.putNextEntry(je);

      // write the bytes of file into jar
      InputStream is = new BufferedInputStream(new FileInputStream(rootLocation + file));
      while ((len = is.read(buffer, 0, buffer.length)) != -1) {
        jos.write(buffer, 0, len);
      }
      is.close();
      jos.closeEntry();
    }
    jos.close();
    System.out.println("Done");
  }
}
