package com.hexastax.kata14.ingest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.hexastax.kata14.model.Ngram;

public class WordListIterator implements Iterator<Ngram> {

  private List<String> words = null;
  private int i = 0;
  private Ngram currNgram = null;
  private boolean done = false;
  private int cardinality = 0;

  public WordListIterator(List<String> words, int cardinality) {
    this.words = words;
    this.cardinality = cardinality;
  }

  @Override
  public boolean hasNext() {
    if (!done) {
      // need to have enough elements in the list to extract
      // list of size 1 is no good for extracting any ngrams

      if (i + cardinality > words.size() || words.size() < 2) {
        done = true;
      } else {
        final int idx = i + cardinality - 1;
        currNgram = new Ngram(words.subList(i, idx), words.get(idx));        
      }
    }
    return !done;
  }

  @Override
  public Ngram next() {
    i++;
    return currNgram;
  }

  @Override
  public void remove() {
  }

  public static void main(String[] args) {
    List<String> words = null;
    WordListIterator iter = null;

    for (int card = 2; card < 7; card++) {
      System.out.println("\n==================================================");
      System.out.println(">> CARDINALITY: " + card + "\n");
      System.out.println("==================================================\n");

       words = Arrays.asList(new String[] { "abc" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);

      words = Arrays.asList(new String[] { "abc", "def" });
      iter = new WordListIterator(words, card);
      dumpNgrams(iter, words);

       words = Arrays.asList(new String[] { "abc", "def", "ghi" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);
      
       words = Arrays.asList(new String[] { "abc", "def", "ghi", "jkl" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);
      
       words = Arrays.asList(new String[] { "abc", "def", "ghi", "jkl", "mno" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);
      
       words = Arrays.asList(new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);

       words = Arrays.asList(new String[] { "abc", "def", "ghi", "jkl", "mno", "pqr", "stu" });
       iter = new WordListIterator(words, card);
       dumpNgrams(iter, words);
       
      System.out.println("==================================================\n");
    }
  }

  private static void dumpNgrams(WordListIterator iter, List<String> input) {
    System.out.println(">> INPUT: " + input);
    System.out.println(">> NGRAMS:");
    while (iter.hasNext()) {
      Ngram ngram = iter.next();
      System.out.println("      >> " + ngram);
    }
    System.out.println(">> -----------------------------");
  }

}