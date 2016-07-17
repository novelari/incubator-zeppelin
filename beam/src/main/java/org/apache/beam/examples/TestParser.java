package org.apache.beam.examples;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * 
 * @author admin
 *
 */
public class TestParser {

  public static void main(String[] args) throws IOException {
    JavaProjectBuilder builder = new JavaProjectBuilder();
    JavaSource src = builder.addSource(new File(
        "/home/admin/mahmoud/work/bigdata/livy-zeppelin/incubator-zeppelin/beam/"
            + "src/main/java/org/apache/beam/examples/MinimalWordCount.java"));

    JavaPackage pkg = src.getPackage();
    List<String> imports = src.getImports(); // {"java.awt.*",
    // "java.util.List"}

    System.out.println(imports.toString());

    // System.out.println(src.getCodeBlock());
    JavaClass class2 = src.getClasses().get(0);
    // System.out.println(class2.getCodeBlock());
    // JavaClass interface1 = src.getClasses().get(2);
  }

}
