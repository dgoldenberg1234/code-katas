package com.hexastax.kata14.ingest;

/**
 * Removes non-ASCII characters from text.
 * 
 * @author dgoldenberg
 */
public class NonAsciiHandler {

  private static final String TEST0 = "dsfkjgdl�fjgl \nfgdfgdf �sdg� \u67e5\u770b\u5168\u90e8 sdfst";

  public static String removeNonAscii(String input) {
    return input.replaceAll("[^\\p{ASCII}]", "");
  }

  public static void main(String[] args) {
    System.out.println(NonAsciiHandler.removeNonAscii(TEST0));
  }
}
