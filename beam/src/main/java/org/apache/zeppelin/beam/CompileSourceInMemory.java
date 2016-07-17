package org.apache.zeppelin.beam;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

import org.apache.log4j.Category;
import org.apache.log4j.Priority;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 *
 */
public class CompileSourceInMemory {
  public static String execute(String className, String code) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

    JavaProjectBuilder builder = new JavaProjectBuilder();
    JavaSource src = builder.addSource(new StringReader(code));

    List<String> imports = src.getImports();
    String importsString = "";
    for (int i = 0; i < imports.size(); i++) {
      importsString += "import " + imports.get(i) + ";\n";
    }
    List<JavaClass> classes = src.getClasses();
    String classesSt = "";
    String classMain = "", classMainName = "";
    for (int i = 0; i < classes.size(); i++) {
      boolean hasMain = false;
      for (int j = 0; j < classes.get(i).getMethods().size(); j++) {
        if (classes.get(i).getMethods().get(j).getName().equals("main")) {
          hasMain = true;
          break;
        }
      }
      if (hasMain == true) {
        classMain = classes.get(i).getCodeBlock() + "\n";
        classMainName = classes.get(i).getName();
      } else
        classesSt += classes.get(i).getCodeBlock() + "\n";

    }
    code = code.replace(classMainName, className);
    // classMain = classMain.replace(classMainName, className);
    // classesSt = classesSt.replace(classMainName, className);
    // className = classMainName;
    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);

    out.println("" 
        // + importsString
        // + classesSt
        // + classMain
        + code);
    // out.println("public class " + className + " {");
    // out.println("  public static void main(String args[]) {");
    // out.println(code);
    // out.println("  }");
    // out.println("}");
    out.close();

    System.out.println(writer.toString());

    JavaFileObject file = new JavaSourceFromString(className, writer.toString());

    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
    ByteArrayOutputStream baosOut = new ByteArrayOutputStream();
    ByteArrayOutputStream baosErr = new ByteArrayOutputStream();
    PrintStream newOut = new PrintStream(baosOut);
    PrintStream newErr = new PrintStream(baosErr);
    // IMPORTANT: Save the old System.out!
    PrintStream oldOut = System.out;
    PrintStream oldErr = System.err;
    // Tell Java to use your special stream
//    System.setErr(new PrintStream(new LoggingOutputStream(Category.getRoot(),
//        Priority.WARN), true));

    System.setOut(newOut);
//    System.setErr(newErr);


    CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

    boolean success = task.call();
    if (!success) {
      for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
        // System.out.println(diagnostic.getCode());
        // System.out.println(diagnostic.getKind());
        // System.out.println(diagnostic.getPosition());
        // System.out.println(diagnostic.getStartPosition());
        // System.out.println(diagnostic.getEndPosition());
        // System.out.println(diagnostic.getSource());
        System.out.println(diagnostic.getMessage(null));

      }
    }
    // System.out.println("Success: " + success);

    if (success) {
      try {
        // Create a stream to hold the output

        // Put things back

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI()
            .toURL() });
        Class.forName(className, true, classLoader)
            .getDeclaredMethod("main", new Class[] { String[].class })
            .invoke(null, new Object[] { null });

        System.out.flush();
        System.err.flush();
        System.setOut(oldOut);
        System.setErr(oldErr);
        // // Show what happened
        // System.out.println("Here: " + baos.toString());
        classLoader.clearAssertionStatus();

        return baosOut.toString();
      } catch (ClassNotFoundException e) {
        e.printStackTrace(newErr);
        System.err.println("Class not found: " + e);
        throw new Exception(baosErr.toString());
      } catch (NoSuchMethodException e) {
        e.printStackTrace(newErr);
        System.err.println("No such method: " + e);
        throw new Exception(baosErr.toString());
      } catch (IllegalAccessException e) {
        e.printStackTrace(newErr);
        System.err.println("Illegal access: " + e);
        throw new Exception(baosErr.toString());
      } catch (InvocationTargetException e) {
        e.printStackTrace(newErr);
        System.err.println("Invocation target: " + e);
        throw new Exception(baosErr.toString());
      }
    } else {
      throw new Exception(baosOut.toString());
    }
  }

}

class JavaSourceFromString extends SimpleJavaFileObject {
  final String code;

  JavaSourceFromString(String name, String code) {
    super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
    this.code = code;
  }

  @Override
  public CharSequence getCharContent(boolean ignoreEncodingErrors) {
    return code;
  }
}
