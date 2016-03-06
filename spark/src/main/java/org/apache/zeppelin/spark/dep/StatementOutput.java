package org.apache.zeppelin.spark.dep;

import java.util.HashMap;
/**
 * 
 *
 */
public class StatementOutput {

  public String status;
  public int executionCount;
  public String ename;
  public String evalue;
  public HashMap<String, String> data;

  public StatementOutput() {
  }

}
