package org.apache.zeppelin.beam;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * @author admin
 *
 */
public class CompileSourceInMemory {
  public static void execute(String className, String code) throws IOException {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

    StringWriter writer = new StringWriter();
    PrintWriter out = new PrintWriter(writer);

    out.println("import org.apache.beam.runners.dataflow.BlockingDataflowPipelineRunner;\n"
        + "import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;\n"
        + "import org.apache.beam.sdk.Pipeline;\n" + "import org.apache.beam.sdk.io.TextIO;\n"
        + "import org.apache.beam.sdk.options.PipelineOptionsFactory;\n"
        + "import org.apache.beam.sdk.transforms.Count;\n"
        + "import org.apache.beam.sdk.transforms.DoFn;\n"
        + "import org.apache.beam.sdk.transforms.MapElements;\n"
        + "import org.apache.beam.sdk.transforms.ParDo;\n"
        + "import org.apache.beam.sdk.transforms.SimpleFunction;\n"
        + "import org.apache.beam.sdk.values.KV;\n");
    out.println("public class " + className + " {");
    out.println("  public static void main(String args[]) {");
    out.println(code);
    out.println("  }");
    out.println("}");
    out.close();
    
    JavaFileObject file = new JavaSourceFromString(className, writer.toString());

    Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
    CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

    boolean success = task.call();
    for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
      System.out.println(diagnostic.getCode());
      System.out.println(diagnostic.getKind());
      System.out.println(diagnostic.getPosition());
      System.out.println(diagnostic.getStartPosition());
      System.out.println(diagnostic.getEndPosition());
      System.out.println(diagnostic.getSource());
      System.out.println(diagnostic.getMessage(null));

    }
    System.out.println("Success: " + success);

    if (success) {
      try {

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI()
            .toURL() });
        Class.forName(className, true, classLoader)
            .getDeclaredMethod("main", new Class[] { String[].class })
            .invoke(null, new Object[] { null });

      } catch (ClassNotFoundException e) {
        System.err.println("Class not found: " + e);
      } catch (NoSuchMethodException e) {
        System.err.println("No such method: " + e);
      } catch (IllegalAccessException e) {
        System.err.println("Illegal access: " + e);
      } catch (InvocationTargetException e) {
        System.err.println("Invocation target: " + e);
      }
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
