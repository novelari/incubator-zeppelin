/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.zeppelin.spark;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import static org.apache.zeppelin.spark.dep.Http.*;

import org.apache.zeppelin.interpreter.Interpreter;
import org.apache.zeppelin.interpreter.InterpreterContext;
import org.apache.zeppelin.interpreter.InterpreterPropertyBuilder;
import org.apache.zeppelin.interpreter.InterpreterResult;
import org.apache.zeppelin.interpreter.InterpreterResult.Code;
import org.apache.zeppelin.spark.dep.Session;
import org.apache.zeppelin.spark.dep.SessionFactory;
import org.apache.zeppelin.spark.dep.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ning.http.client.Response;

/**
 * Spark Yarn Cluster interpreter for Zeppelin.
 *
 */
public class SparkYarnClusterInterpreter extends Interpreter {

  private Session session = null;
  private final String host;

  public SparkYarnClusterInterpreter(Properties property) {
    super(property);
    host = getProperty("livy.server.host");
  }

  public static Logger logger = LoggerFactory.getLogger(SparkYarnClusterInterpreter.class);

  static {

    Interpreter
        .register(
            "yspark",
            "spark",
            SparkYarnClusterInterpreter.class.getName(),
            new InterpreterPropertyBuilder()
                .add("spark.app.name",
                    SparkInterpreter.getSystemDefault(null, "spark.app.name",
                    "Zeppelin_yarn_cluster"), "The name of spark application.")
                .add("master",
                    SparkInterpreter.getSystemDefault(
                    null, "spark.master", "yarn-cluster"),
                    "Spark master to run in yarn-cluster mode")
                .add("livy.server.host",
                    SparkInterpreter.getSystemDefault(
                    null, "livy.server.host", "localhost:8998"),
                    "The host of livy server.")
                .add("spark.driver.cores", SparkInterpreter.getSystemDefault(
                    null, "spark.driver.cores", "1"),
                    "Driver cores. ex) 1, 2")
                .add("spark.driver.memory", SparkInterpreter.getSystemDefault(
                    null, "spark.driver.memory", "512m"),
                    "Driver memory. ex) 512m, 32g")
                .add("spark.executor.instances",
                    SparkInterpreter.getSystemDefault(null, "spark.executor.instances", "3"),
                    "Executor instances. ex) 1, 4")
                .add("spark.executor.cores", SparkInterpreter.getSystemDefault(
                    null, "spark.executor.cores", "1"),
                    "Num cores per executor. ex) 1, 4")
                .add("spark.executor.memory",
                     SparkInterpreter.getSystemDefault(
                     null, "spark.executor.memory", "512m"),
                    "Executor memory per worker instance. ex) 512m, 32g")
                .add("spark.dynamicAllocation.enabled",
                     SparkInterpreter.getSystemDefault(
                     null, "spark.dynamicAllocation.enabled", "false"),
                    "Use dynamic resource allocation")
                .add(
                    "spark.dynamicAllocation.cachedExecutorIdleTimeout",
                    SparkInterpreter.getSystemDefault(
                    null, "spark.dynamicAllocation.cachedExecutorIdleTimeout",
                    "120s"), "Remove an executor which has cached data blocks")
                .add("spark.dynamicAllocation.minExecutors",
                     SparkInterpreter.getSystemDefault(
                     null, "spark.dynamicAllocation.minExecutors", "0"),
                    "Lower bound for the number of executors if dynamic allocation is enabled. ")
                .add("spark.dynamicAllocation.initialExecutors",
                     SparkInterpreter.getSystemDefault(
                     null, "spark.dynamicAllocation.initialExecutors", "1"),
                    "Initial number of executors to run if dynamic allocation is enabled. ")
                .add("spark.dynamicAllocation.maxExecutors",
                     SparkInterpreter.getSystemDefault(
                     null, "spark.dynamicAllocation.maxExecutors", "10"),
                    "Upper bound for the number of executors if dynamic allocation is enabled. ")
                .build());
  }


  @Override
  public void open() {
  }

  @Override
  public void close() {
    if (session != null) {
      SessionFactory.deleteSession(session);
    }
  }

  private boolean checkLivyServer() {
    try {
      Response r = get(host);
      if (r.hasResponseStatus()) {
        return true;
      }
      return false;
    } catch (Exception e) {
      logger.info("Interpreter exception", e);
      return false;
    }
  }

  @Override
  public InterpreterResult interpret(String st, InterpreterContext context) {

    if (!getProperty("master").equals("yarn-cluster"))
      return new InterpreterResult(Code.ERROR,
                 "The master mode must be yarn-cluster not " + getProperty("master") + " .");
// Check if livy server is running in that host
    if (!checkLivyServer()) {
      return new InterpreterResult(Code.ERROR,
          "you need to have the livy server running in the master node of the cluster " +
            "and set the property:  livy.server.host to <master-node-hostname-or-ip>:8998");
    }

    

    if (session != null) {
      try {
        session = SessionFactory.getSession(session);
      } catch (IOException e) {
        logger.info("Interpreter exception", e);
        return new InterpreterResult(Code.ERROR,
            "you need to have the livy server running in the master node of the cluster " +
            "and set the property:  livy.server.host to <master-node-hostname-or-ip>:8998");
      }
    }
    if (session == null) {
      try {
        session = SessionFactory.createSession(host, getProperty());

        if (session == null) {
          return new InterpreterResult(Code.ERROR, "Can not create a session, please try again.");
        }

        if (session.state.equals("error")) {
          SessionFactory.deleteSession(session);

          return new InterpreterResult(Code.ERROR,
              "Resources aren't enough or error happened while creating session,"
              + " please try again.");
        }

      } catch (IOException e) {
        logger.info("Interpreter exception", e);
        return new InterpreterResult(Code.ERROR,
            "you need to have the livy server running in the master node of the cluster " +
            "and set the property:  livy.server.host to <master-node-hostname-or-ip>:8998");
      }
    }
    Statement statement = new Statement();
    try {
      statement = session.createStatement(st);
    } catch (IOException e) {
      logger.info("Interpreter exception", e);
      return new InterpreterResult(Code.ERROR, "Can not create a statement, please try again.");

    }
    if (statement.state.equals("available") && statement.output.status.equals("ok")) {
      return new InterpreterResult(Code.SUCCESS, statement.output.data.get("text/plain"));
    }

    return new InterpreterResult(Code.ERROR, statement.output.evalue);
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
    return 100;
  }

  @Override
  public List<String> completion(String buf, int cursor) {
    return null;
  }

}
