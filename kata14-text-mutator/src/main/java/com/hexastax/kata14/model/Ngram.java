package com.hexastax.kata14.model;

import java.util.List;

/**
 * Represents an "N-gram": a pairing of a word sequence (first part) followed by another word
 * (second part).
 * 
 * @author dgoldenberg
 */
public class Ngram {

  private String id = null;
  private List<String> first = null;
  private String second = null;

  public Ngram(List<String> first, String second) {
    this.first = first;
    this.second = second;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public List<String> getFirst() {
    return first;
  }

  public void setSecond(String second) {
    this.second = second;
  }

  public String getSecond() {
    return second;
  }

  public String getFirstAsSummary() {
    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < first.size(); i++) {
      String item = first.get(i);
      if (i > 0) {
        buff.append(":");
      }
      buff.append(item);
    }
    return buff.toString();
  }

  @Override
  public String toString() {
    return "(" + first + "), " + second;
  }
}
