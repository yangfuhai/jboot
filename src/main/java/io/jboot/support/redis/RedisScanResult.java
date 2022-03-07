package io.jboot.support.redis;


import java.io.Serializable;
import java.util.List;

public class RedisScanResult<T> implements Serializable {
  private String cursor;
  private List<T> results;

  public RedisScanResult() {

  }

  public RedisScanResult(String cursor, List<T> results) {
    this.cursor = cursor;
    this.results = results;
  }

  public String getCursor() {
    return cursor;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  public List<T> getResults() {
    return results;
  }

  public void setResults(List<T> results) {
    this.results = results;
  }

  public boolean isCompleteIteration() {
    return "0".equals(getCursor());
  }

}
