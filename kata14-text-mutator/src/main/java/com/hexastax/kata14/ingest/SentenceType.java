package com.hexastax.kata14.ingest;

import org.apache.commons.lang.StringUtils;

/**
 * Defines sentence types.
 * 
 * @author dgoldenberg
 */
public enum SentenceType implements MiscConstants {

  // A walk in the park.
  STATEMENT(1, true, StringUtils.EMPTY, PERIOD, "statement"),

  // A walk in the park?
  QUESTION(2, true, StringUtils.EMPTY, QUESTION_MARK, "question"),

  // A walk in the park!
  EXCLAMATION(3, true, StringUtils.EMPTY, EXCLAMATION_POINT, "exclamation"),

  // "A walk in the park."
  QUOTED_STATEMENT(4, true, QUOTE, PERIOD_QUOTE, "quoted statement"),

  // "A walk in the park?"
  QUOTED_QUESTION(5, true, QUOTE, QUESTION_MARK_QUOTE, "quoted question"),

  // "A walk in the park!"
  QUOTED_EXCLAMATION(6, true, QUOTE, EXCLAMATION_POINT_QUOTE, "quoted exclamation"),

  // "A walk in the park", he said.
  STATEMENT_QUOTE(7, false, QUOTE, PERIOD, "statement with quote"),

  // "A walk in the park", he said?
  QUESTION_QUOTE(8, false, QUOTE, QUESTION_MARK, "question with quote"),

  // "A walk in the park", he said!
  EXCLAMATION_QUOTE(9, false, QUOTE, EXCLAMATION_POINT, "exclamation with quote"),

  // Anything else
  OTHER(10, false, null, null, "other/unrecognized");

  private int type;
  private boolean handled;
  private String sentenceStarter;
  private String sentenceCloser;
  private String description;

  private SentenceType(int type, boolean handled, String starter, String closer, String description) {
    this.type = type;
    this.handled = handled;
    this.sentenceStarter = starter;
    this.sentenceCloser = closer;
    this.description = description;
  }

  public int getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public boolean isHandled() {
    return handled;
  }

  public String getSentenceStarter() {
    return sentenceStarter;
  }

  public String getSentenceCloser() {
    return sentenceCloser;
  }

  public static SentenceType fromTypeValue(int value) {
    SentenceType type = null;
    SentenceType[] values = values();
    if (value > 0 && value <= values.length) {
      type = values[value - 1];
    }
    return type;
  }

  public String reconstituteSentence(String sentence) {
    String munged = sentence;
    if (handled) {
      munged = String.format("%s%s%s", sentenceStarter, sentence, sentenceCloser);
    }
    return munged;
  }

  public static SentenceType getSentenceType(String sentence) {
    SentenceType type = OTHER;

    if (sentence.startsWith(QUOTE)) {
      if (sentence.endsWith(PERIOD_QUOTE)) {
        type = QUOTED_STATEMENT;
      } else if (sentence.endsWith(QUESTION_MARK_QUOTE)) {
        type = QUOTED_QUESTION;
      } else if (sentence.endsWith(EXCLAMATION_POINT_QUOTE)) {
        type = QUOTED_EXCLAMATION;
      } else if (sentence.endsWith(PERIOD)) {
        type = STATEMENT_QUOTE;
      } else if (sentence.endsWith(QUESTION_MARK)) {
        type = QUESTION_QUOTE;
      } else if (sentence.endsWith(EXCLAMATION_POINT)) {
        type = EXCLAMATION_QUOTE;
      }
    } else if (sentence.endsWith(PERIOD)) {
      type = STATEMENT;
    } else if (sentence.endsWith(QUESTION_MARK)) {
      type = QUESTION;
    } else if (sentence.endsWith(EXCLAMATION_POINT)) {
      type = EXCLAMATION;
    }

    return type;
  }
}
