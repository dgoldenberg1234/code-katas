package com.hexastax.kata14.ingest;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * Extracts words from text.
 * 
 * @author dgoldenberg
 */
public class WordExtractor {

  private static final String TEST1 = "the quick brown - fox jumps over the lazy dog";

  // TODO this can be a configuration item
  // TODO dashes (" - ") should be tokenized out earlier when we handle punctuation and such
  private static final List<String> STOPWORDS = Arrays.asList(new String[] { "-" });

  public static List<String> getWords(String input) {
    List<String> words = new ArrayList<String>();

    BreakIterator boundary = BreakIterator.getWordInstance(Locale.US);
    boundary.setText(input);
    int start = boundary.first();

    for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
      String item = input.substring(start, end);
      if (StringUtils.isNotBlank(item) && !STOPWORDS.contains(item)) {
        words.add(item);
      }
    }
    return words;
  }

  public static void main(String[] args) {
    List<String> words = WordExtractor.getWords(TEST1);
    for (String word : words) {
      System.out.println(">> [" + word + "]");
    }
  }

}
