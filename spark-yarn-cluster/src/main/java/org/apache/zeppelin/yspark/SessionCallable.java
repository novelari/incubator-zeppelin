package org.apache.zeppelin.yspark;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
/**
 * some comments
 *
 */
public class SessionCallable implements Callable<Session> {
  int count = 0;
  private Session session;

  public SessionCallable(Session session) {
    this.session = session;
  }

  @Override
  public Session call() throws Exception {
    while (!this.session.state.equals("idle")) {
      this.session = SessionFactory.getSession(this.session);
      TimeUnit.MILLISECONDS.sleep(2000);
    }
    return this.session;
  }
}
