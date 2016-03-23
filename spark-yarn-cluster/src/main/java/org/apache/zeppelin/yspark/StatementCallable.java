package org.apache.zeppelin.yspark;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
/**
 * some comments
 *
 */
public class StatementCallable implements Callable<Statement> {
  int count = 0;
  private Session session;
  private Statement statement;

  public StatementCallable(Session session, Statement statement) {
    this.session = session;
    this.statement = statement;
  }

  @Override
  public Statement call() throws Exception {
    while (this.statement.state.equals("running")) {
      this.statement = this.session.getStatement(this.statement);
      TimeUnit.MILLISECONDS.sleep(2000);
    }
    return this.statement;
  }
}
