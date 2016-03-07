package com.hexastax.kata14.ingest;

/**
 * Removes punctuation characters.
 * 
 * @author dgoldenberg
 */
public class PunctuationHandler {

  private static final String TEST1 = "\"the quick, <brown>-fox jumps*&#@!~`/=+\\|%^ ()over the 123 lazy dog's body.?";

  public static String removePunctuation(String input) {
    // Apostrophes are good to have e.g. Buster's, Majesty's
    // Hyphens may be good e.g. up-to-date. But TODO we don't want "look - there" (this is a dash).
    // TODO Period's are good to have sometimes, mid-sentence cases: Mr., www.google.com
    return input.replaceAll("(?!['-])\\p{Punct}", "");
  }

  public static void main(String[] args) {
    System.out.println(PunctuationHandler.removePunctuation(TEST1));
  }

}
