package org.apache.zeppelin.spark.dep;

import java.util.HashMap;
/**
 * StatementOutput
 *
 */
public class StatementOutput {

  public String status;
  int executionCount;
  String ename;
  public String evalue;
  public HashMap<String, String> data;

  public StatementOutput() {
  }

}
