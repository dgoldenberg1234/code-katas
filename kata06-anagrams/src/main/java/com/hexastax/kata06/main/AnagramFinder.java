package com.hexastax.kata06.main;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Finds anagrams of words in the input dictionary. The dictionary has one word per line.
 * <p>
 * Represents an implementation of Kata 6 from here: http://codekata.com/kata/kata06-anagrams.
 * 
 * @author dgoldenberg
 */
@SuppressWarnings("unchecked")
public class AnagramFinder {

  // Characters that are used in words in the dictionary.
  // The index of each character in this array is the "feature ID" of the character.
  private static final char[] CHARS = new char[] {
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
    'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
    'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\''
  };

  // Maps a character to the respective "feature ID" for the character.
  private static Map<Character, Integer> charsToFeatureIds = new HashMap<Character, Integer>();

  // Maps a feature set to the list of words which "have these features" i.e. words that consist of
  // a given set of characters.
  private Map<List<Integer>, Set<String>> featureSetsToWords = new HashMap<List<Integer>, Set<String>>();

  // Represents an empty feature set (for a word in the dictionary that we want to ignore e.g. one
  // with non-ASCII).
  private static final List<Integer> EMPTY_FEATURE_SET = (List<Integer>) Collections.EMPTY_LIST;

  public AnagramFinder() {
    for (int idx = 0; idx < CHARS.length; idx++) {
      charsToFeatureIds.put(CHARS[idx], idx);
    }
  }

  /**
   * Finds anagrams and prints out some stats.
   * 
   * @param inStream
   *          the input stream for the dictionary
   */
  public void findAnagrams(InputStream inStream) {

    long startTime = System.currentTimeMillis();

    try (Scanner scan = new Scanner(inStream)) {
      // For each line
      while (scan.hasNextLine()) {
        String word = scan.nextLine().trim();
        if (StringUtils.isNotEmpty(word)) {
          List<Integer> featureSet = toFeatures(word);
          if (!featureSet.isEmpty()) {
            Set<String> words = featureSetsToWords.get(featureSet);
            if (words == null) {
              words = new HashSet<String>();
              featureSetsToWords.put(featureSet, words);
            }
            words.add(word);
          }
        }
      }

      long totalNumWords = 0L;
      long totalNumAnagrams = 0L;
      List<Integer> longestAnagramsKey = null;
      List<Integer> mostWordsAnagramsKey = null;

      // For each entry in the internal map
      for (Map.Entry<List<Integer>, Set<String>> entry : featureSetsToWords.entrySet()) {
        List<Integer> featureSet = entry.getKey();
        Set<String> words = entry.getValue();

        // Count the total number of words
        totalNumWords += words.size();

        // Having more than one word per feature set is a sign of an anagram
        if (words.size() > 1) {
          // Count # anagrams
          totalNumAnagrams++;

          // Find the longest words that are anagrams
          // (TODO can keep a list of these if there're more than 1 max)
          if (longestAnagramsKey == null || featureSet.size() > longestAnagramsKey.size()) {
            longestAnagramsKey = featureSet;
          }

          // Find the set of anagrams containing the most words.
          // (TODO can keep a list of these if there're more than 1 max)
          if (mostWordsAnagramsKey == null || words.size() > mostWordsAnagramsKey.size()) {
            mostWordsAnagramsKey = featureSet;
          }
        }
      }

      long finishTime = System.currentTimeMillis();

      System.out.println(">> Total # words                  : " + totalNumWords);
      System.out.println(">> Total # anagrams               : " + totalNumAnagrams);
      System.out.println(">> Longest anagrams               : " + ((longestAnagramsKey == null)
        ? "none" : featureSetsToWords.get(longestAnagramsKey)));
      System.out.println(">> Anagrams with the most # words : " + ((mostWordsAnagramsKey == null)
        ? "none" : featureSetsToWords.get(mostWordsAnagramsKey)));
      System.out.println();
      System.out.println(">> Exec. time                     : " + (finishTime - startTime) + " ms.");

      // dumpAnagrams();

    } catch (Exception ex) {
      // TODO log or rethrow
      ex.printStackTrace();
    }
  }

  // Maps a given word to a feature set that represents it (based on the characters of the word,
  // where each character is a "feature").
  private static List<Integer> toFeatures(String word) {
    List<Integer> features = new ArrayList<Integer>(word.length());

    for (int idx = 0; idx < word.length(); idx++) {
      char ch = word.charAt(idx);
      Integer featureId = charsToFeatureIds.get(ch);
      if (featureId == null) {
        features = EMPTY_FEATURE_SET;
        break;
      }
      features.add(featureId);
    }
    Collections.sort(features);

    return features;
  }

  protected void dumpAnagrams() {
    for (Map.Entry<List<Integer>, Set<String>> entry : featureSetsToWords.entrySet()) {
      Set<String> words = entry.getValue();
      if (words.size() > 1) {
        System.out.println(">> anagrams: " + words);
      }
    }
  }

  public static void main(String[] args) {
    // or small-wordlist.txt
    new AnagramFinder().findAnagrams(AnagramFinder.class.getResourceAsStream("/wordlist.txt"));
  }

}
