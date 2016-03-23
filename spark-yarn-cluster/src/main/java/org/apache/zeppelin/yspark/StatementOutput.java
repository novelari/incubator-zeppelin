package org.apache.zeppelin.yspark;

import java.util.HashMap;
/**
 * StatementOutput
 *
 */
public class StatementOutput {

  String status;
  int executionCount;
  String ename;
  String evalue;
  HashMap<String, String> data;

  public StatementOutput() {
  }

}
