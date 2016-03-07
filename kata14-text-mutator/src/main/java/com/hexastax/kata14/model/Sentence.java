package com.hexastax.kata14.model;

import com.hexastax.kata14.ingest.SentenceType;

/**
 * Represents a sentence in a text model.
 * 
 * @author dgoldenberg
 */
public class Sentence {

  private String id;
  private SentenceType type;

  public Sentence(Paragraph para, SentenceType type, int sentNum) {
    this.type = type;
    this.id = String.format("%d:%s", sentNum, para.getId());
  }

  public Sentence(String id, SentenceType type) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public SentenceType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Sentence [id=" + id + ", type=" + type + "]";
  }
}
