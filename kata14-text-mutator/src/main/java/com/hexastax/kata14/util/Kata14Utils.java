package com.hexastax.kata14.util;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Contains utility methods.
 * 
 * @author dgoldenberg
 */
public class Kata14Utils {

  public static final int MAX_NGRAM_CARDINALITY = 10;

  public static void validateCardinality(int ngramCard) {
    if (ngramCard < 2) {
      throw new IllegalArgumentException("Invalid ngram cardinality value: " + ngramCard + " (is less than 2).");
    }
    if (ngramCard > MAX_NGRAM_CARDINALITY) {
      throw new IllegalArgumentException("Invalid ngram cardinality value: " + ngramCard + " (is greater than max value of " + MAX_NGRAM_CARDINALITY + ").");
    }
  }

  public static void main(String[] args) {
    Map<String, Double> weights = new HashMap<String, Double>();
    weights.put("AAA", 20d);
    weights.put("BBB", 20.2d);
    weights.put("CCC", 100d);
    weights.put("DDD", 0d);
    weights.put("EEE", 1d);
    weights.put("FFF", 99d);
    weights.put("GGG", 15d);
    weights.put("HHH", 50d);
    weights.put("III", 88d);
    weights.put("JJJ", 8d);
    weights.put("KKK", 12d);
    weights.put("LLL", 99d);
    weights.put("MMM", 77d);
    weights.put("NNN", 9d);
    weights.put("OOO", 1d);
    weights.put("PPP", 34d);

    ExtendedRandom random = new ExtendedRandom();

    SortedMap<String, Integer> sel = new TreeMap<String, Integer>();
    for (int i = 0; i < 500; i++) {
      String selected = random.getWeightedRandom(weights);
      // System.out.println(">> " + selected);

      Integer c = sel.get(selected);
      if (c == null) {
        sel.put(selected, 1);
      } else {
        sel.put(selected, c + 1);
      }
    }
    for (Map.Entry<String, Integer> e : sel.entrySet()) {
      System.out.println(">> " + e.getKey() + " -- " + e.getValue());
    }
  }

}
