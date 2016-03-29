package org.apache.zeppelin.spark.dep;

import static org.apache.zeppelin.spark.dep.Http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.ning.http.client.Response;
/**
 * 
 *
 */
public class Session {

  public int id;
  public String name;
  public String state;
  public String kind;
  public String[] log;
  public String url;
  public String driverMemory;
  public String driverCores;
  public String executorMemory;
  public String executorCores;
  public String numExecutors;
  

  public Session() {
  }

  public Statement createStatement(String st) throws IOException {
    HashMap<String, String> command = new HashMap();
    Gson gson = new Gson();
    command.put("code", st);
    String data = gson.toJson(command);
    Response r = post(this.url + "/statements", data);
    String json = r.getResponseBody();
    Statement statement = gson.fromJson(json, Statement.class);
    Callable callableTask = new StatementCallable(this, statement);
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<Statement> future = executor.submit(callableTask);
    try {
      statement = future.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    executor.shutdown();
    return statement;
  }

  public Statement getStatement(Statement statement) throws IOException {
    Response r = get(this.url + "/statements/" + statement.id);
    String json = r.getResponseBody();
    Gson gson = new Gson();
    statement = gson.fromJson(json, Statement.class);
    return statement;
  }

}
