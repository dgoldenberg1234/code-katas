package com.hexastax.kata14.model;

import java.io.InputStream;

/**
 * Represents a corpus document.
 * 
 * @author dgoldenberg
 */
public class CorpusDocument {

  private String id = null;
  private String name = null;
  private InputStream stream = null;

  public CorpusDocument(String corpusName, String name, InputStream stream) {
    this.name = name;

    String corpusKey = corpusName.replaceAll("\\W", "").toLowerCase();
    String nameKey = name.replaceAll("\\W", "").toLowerCase();
    if (nameKey.length() > 10) {
      nameKey = nameKey.substring(0, 10);
    }
    this.id = String.format("%s-%s", nameKey, corpusKey).substring(0, 12);

    this.stream = stream;
  }

  public CorpusDocument(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public InputStream getStream() {
    return stream;
  }

  @Override
  public String toString() {
    return "CorpusDocument [id=" + id + ", name=" + name + "]";
  }
}
