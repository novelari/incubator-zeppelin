package org.apache.zeppelin.beam;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.some;
import static java.lang.System.getProperty;
import static javarepl.Result.result;
import static javarepl.console.ConsoleConfig.consoleConfig;
import static javax.tools.ToolProvider.getSystemJavaCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javarepl.Repl;
import javarepl.client.EvaluationResult;
import javarepl.client.JavaREPLClient;
import javarepl.console.ConsoleConfig;
import javarepl.console.SimpleConsole;
import javarepl.console.commands.EvaluateFromHistory;
import javarepl.console.commands.ListValues;
import javarepl.console.commands.SearchHistory;
import javarepl.console.commands.ShowHistory;
import javarepl.console.rest.RestConsole;

import com.google.gson.Gson;
import com.googlecode.totallylazy.Option;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Param;
import com.ning.http.client.Response;

/**
 * 
 * @author admin
 *
 */
public class ReplServer {

  JavaREPLClient client = null;
  private String url;
  private ResultPrinter console = new ResultPrinter(true);
  private Option<EvaluationResult> result = null;
  private static Option<Process> process = none();
  static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
  static Gson gson = new Gson();

  public ReplServer(String host, int port) throws Exception {
    url = "http://" + host + ":" + port;
    JavaREPLClient client = startNewLocalInstance(host, port);
  }

  public List<Map<String, String>> execute(String expr) {
    final AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(url
        + "/execute");

//    builder.addHeader("Content-Type", "application/json");
    List<Param> expression = new ArrayList<>();
    expression.add(new Param("expression", expr));
    
    builder.setFormParams(expression);
//    builder.setBody("{\"expression\": \" " + expr + "\"}");
    Future<Response> f = builder.execute();
    try {
      String json = f.get().getResponseBody();
      System.out.println(json);
      return gson.fromJson(json, ExpressionResult.class).logs;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // public static void main(String[] args) throws Exception {
  // JavaREPLClient client = startNewLocalInstance("localhost", 4444);
  //
  //
  // // result = client.execute("import org.apache.beam.examples.DebuggingWordCount;");
  // result = client.execute("System.out.println(\"Hello\")");
  // console.printEvaluationResult(result.get());
  // while(true){
  //
  // }
  // }
  public void startServer(String host, int port) throws Exception {
    JavaREPLClient client = startNewLocalInstance("localhost", 4444);
  }

  private boolean waitUntilInstanceStarted(JavaREPLClient client) throws Exception {
    for (int i = 0; i < 500; i++) {
      Thread.sleep(10);
      if (client.status().isRunning()) {
        return true;
      }
    }

    return false;
  }

  private JavaREPLClient startNewLocalInstance(String hostname, Integer port) throws Exception {
    if (getSystemJavaCompiler() == null) {
      console.printError("\nERROR: Java compiler not found.\n"
          + "This can occur when JavaREPL was run with JRE instead of JDK "
          + "or JDK is not configured correctly.");
      System.exit(0);
    }

    console.printInfo("Type expression to evaluate, \u001B[32m:help\u001B[0m for more options"
        + " or press \u001B[32mtab\u001B[0m to auto-complete.");

    ProcessBuilder builder = new ProcessBuilder("java", "-cp",
        System.getProperty("java.class.path"), Repl.class.getCanonicalName(), "--port=" + port);
    builder.redirectErrorStream(true);

    process = some(builder.start());
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        console.printInfo("\nTerminating...");
        process.get().destroy();
      }
    });

    JavaREPLClient replClient = new JavaREPLClient(hostname, port);
    if (!waitUntilInstanceStarted(replClient)) {
      console.printError("\nERROR: Could not start REPL instance at http://" + hostname + ":"
          + port);
      System.exit(0);
    }

    return replClient;
  }

  public void avfr(String... args) throws Exception {
    ConsoleConfig config = consoleConfig()
        .historyFile(new File(getProperty("user.home"), ".javarepl-embedded.history"))
        .commands(ListValues.class, ShowHistory.class, EvaluateFromHistory.class,
            SearchHistory.class).results(result("date", new Date()), result("num", 42));

    new RestConsole(new SimpleConsole(config), 8001);
  }
}
