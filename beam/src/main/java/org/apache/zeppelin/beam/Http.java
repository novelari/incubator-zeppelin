package org.apache.zeppelin.beam;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

/**
 * 
 * */
public class Http {

  static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
  static Gson gson = new Gson();

  private static Response getFromFuture(Future<Response> f) {
    try {
      Response r = f.get();
      return r;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Response get(String url) {
    Future<Response> f = asyncHttpClient.prepareGet(url).execute();
    return getFromFuture(f);
  }

  public static ExpressionResult execute(String host, String expr) {
    final AsyncHttpClient.BoundRequestBuilder builder = asyncHttpClient.preparePost(host
        + "/execute");

    builder.addHeader("Content-Type", "application/json");
    builder.setBody("{\"expression\": \" " + expr + "\"}");
    Future<Response> f = builder.execute();
    try {
      String json = f.get().getResponseBody();
      return gson.fromJson(json, ExpressionResult.class);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Response delete(String url) {
    Future<Response> f = asyncHttpClient.prepareDelete(url).execute();
    return getFromFuture(f);
  }

}

