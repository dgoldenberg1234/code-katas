package com.hexastax.kata14.ingest;

/**
 * Determines the type for a given sentence, e.g. statement, question, quoted statement, etc.
 * 
 * @author dgoldenberg
 */
public class SentenceClassifier {

  private static final String TEST0 = "A walk in the park.";
  private static final String TEST1 = "A walk in the park?";
  private static final String TEST2 = "A walk in the park!";
  private static final String TEST3 = "\"A walk in the park.\"";
  private static final String TEST4 = "\"A walk in the park?\"";
  private static final String TEST5 = "\"A walk in the park!\"";
  private static final String TEST6 = "\"A walk in the park\", he said.";
  private static final String TEST7 = "\"A walk in the park\", he said?";
  private static final String TEST8 = "\"A walk in the park\", he said!";
  private static final String TEST9 = "a walk in the park";

  public static SentenceType getSentenceType(String sentence) {
    return SentenceType.getSentenceType(sentence);
  }

  public static void main(String[] args) {
    dumpSentenceType(TEST0);
    dumpSentenceType(TEST1);
    dumpSentenceType(TEST2);
    dumpSentenceType(TEST3);
    dumpSentenceType(TEST4);
    dumpSentenceType(TEST5);
    dumpSentenceType(TEST6);
    dumpSentenceType(TEST7);
    dumpSentenceType(TEST8);
    dumpSentenceType(TEST9);
  }

  private static void dumpSentenceType(String input) {
    System.out.println(">> " + input + "  ----  " + SentenceClassifier.getSentenceType(input).getDescription());
  }

}
