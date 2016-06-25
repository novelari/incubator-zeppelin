package org.apache.zeppelin.beam;

import static javarepl.Result.result;
import static javarepl.console.ConsoleConfig.consoleConfig;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javarepl.console.ConsoleConfig;
import javarepl.console.SimpleConsole;
import javarepl.console.commands.EvaluateFromHistory;
import javarepl.console.commands.ListValues;
import javarepl.console.commands.SearchHistory;
import javarepl.console.commands.ShowHistory;
import javarepl.console.rest.RestConsole;

import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterPropertyBuilder;
import org.apache.zeppelin.interpreter.InterpreterResult;

import com.google.gson.Gson;

/**
*
*/
public class BeamInterpreter extends Interpreter {

  private ReplServer replServer = null;
  private String host = "http://localhost:8001";

  public BeamInterpreter(Properties property) {
    super(property);
  }

  static {
    Interpreter.register("beam", "beam", BeamInterpreter.class.getName(),
        new InterpreterPropertyBuilder().build());
  }

  public static void main(String[] args) {
    InterpreterResult x = new BeamInterpreter(null).interpret(
        "System.out.print(\"Hellgffgjo\");System.out.print(\"Hello\");", null);
  }

  @Override
  public void open() {
    // ConsoleConfig config = consoleConfig()
    // .historyFile(new File(getProperty("user.home"), ".javarepl-embedded.history"))
    // .commands(ListValues.class, ShowHistory.class, EvaluateFromHistory.class,
    // SearchHistory.class).results(result("date", new Date()), result("num", 42));
    //
    // try {
    // new RestConsole(new SimpleConsole(config), 8001);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // try {
    // replServer = new ReplServer("localhost", 5555);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

  }

  @Override
  public void close() {

  }

  // @Override
  // public InterpreterResult interpret(String st, InterpreterContext context) {
  //
  // List<Map<String, String>> logs = replServer.execute(st);
  // String successfullyMsg = "";
  // for (int i = 0; i < logs.size(); i++) {
  // if (logs.get(i).get("type").equals("ERROR")) {
  // return new InterpreterResult(InterpreterResult.Code.ERROR, logs.get(i).get("message"));
  // } else if (logs.get(i).get("type").equals("SUCCESS") &&
  // !logs.get(i).get("message").isEmpty()) {
  // successfullyMsg += logs.get(i).get("message") + "\n";
  // }
  //
  // }
  // return new InterpreterResult(InterpreterResult.Code.SUCCESS, successfullyMsg);
  //
  // }
  @Override
  public InterpreterResult interpret(String st, InterpreterContext context) {

    String uuid = "C" + UUID.randomUUID().toString().replace("-", "");
    // System.out.println(uuid);
    // StringWriter writer = new StringWriter();
    // PrintWriter out = new PrintWriter(writer);
    //
    // out.println("import org.apache.beam.runners.dataflow.BlockingDataflowPipelineRunner;\n"
    // + "import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;\n"
    // + "import org.apache.beam.sdk.Pipeline;\n" + "import org.apache.beam.sdk.io.TextIO;\n"
    // + "import org.apache.beam.sdk.options.PipelineOptionsFactory;\n"
    // + "import org.apache.beam.sdk.transforms.Count;\n"
    // + "import org.apache.beam.sdk.transforms.DoFn;\n"
    // + "import org.apache.beam.sdk.transforms.MapElements;\n"
    // + "import org.apache.beam.sdk.transforms.ParDo;\n"
    // + "import org.apache.beam.sdk.transforms.SimpleFunction;\n"
    // + "import org.apache.beam.sdk.values.KV;\n");
    // out.println("public class " + uuid + " {");
    // out.println("  public static void main(String args[]) {");
    // out.println(st);
    // out.println("  }");
    // out.println("}");
    // out.close();
    //
    // System.out.println(writer.toString());
    // Class<?> class1;
    try {
      // class1 = Compiler.compile(uuid, writer.toString());
      // Compiler.invokeMain(class1);
      CompileSourceInMemory.execute(uuid, st);
    } catch (Exception e) {
      e.printStackTrace();
      return new InterpreterResult(InterpreterResult.Code.ERROR, e.getMessage());

    }

    return new InterpreterResult(InterpreterResult.Code.SUCCESS, "");

  }

  @Override
  public void cancel(InterpreterContext context) {

  }

  @Override
  public FormType getFormType() {
    return FormType.SIMPLE;
  }

  @Override
  public int getProgress(InterpreterContext context) {
    return 0;
  }

  @Override
  public List<String> completion(String buf, int cursor) {
    return null;
  }

}
