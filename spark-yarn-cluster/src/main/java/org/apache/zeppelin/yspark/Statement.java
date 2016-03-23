package org.apache.zeppelin.yspark;

import static org.apache.zeppelin.yspark.Http.*;

import java.io.IOException;

import com.google.gson.Gson;
import com.ning.http.client.Response;
/**
 * some comments
 *
 */
public class Statement {

  int id;
  String state;
  StatementOutput output;

  public Statement() {
  }

}
