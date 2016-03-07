package com.hexastax.kata14.model;

/**
 * Represents a paragraph in a text model.
 * 
 * @author dgoldenberg
 */
public class Paragraph {

  private String id;
  private int num;

  public Paragraph(CorpusDocument doc, int num) {
    this.num = num;
    this.id = String.format("%d:%s", num, doc.getId()).substring(0, 12);
  }

  public Paragraph(String id, int num) {
    this.id = id;
    this.num = num;
  }

  public String getId() {
    return id;
  }

  public int getNum() {
    return num;
  }

  @Override
  public String toString() {
    return "Paragraph [id=" + id + ", num=" + num + "]";
  }
}
