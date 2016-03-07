package com.hexastax.kata14.ingest;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Breaks text up into sentences.
 * 
 * @author dgoldenberg
 */
public class SentenceBreaker {

  private static final String TEST0 = "First sentence.";
  private static final String TEST1 = "  First sentence. Second sentence. ";
  private static final String TEST2 = "First sentence! Second sentence?";
  private static final String TEST3 = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";
  private static final String TEST4 = "the quick brown fox jumps over the lazy dog";

  private SentenceDetector sd;

  public SentenceBreaker() throws IOException {
    InputStream stream = getClass().getResourceAsStream("/sentences/dg-en-sent.bin");

    SentenceModel sm = new SentenceModel(stream);
    sd = new SentenceDetectorME(sm);
    stream.close();
  }

  public Span[] getStarts(String text) {
    return sd.sentPosDetect(text);
  }

  public Iterator<String> iterator(String text) {
    return new OuterSentenceBreakerIterator(new SentenceDetectorIterator(text));
  }

  private class OuterSentenceBreakerIterator implements Iterator<String> {

    private SentenceDetectorIterator inner = null;
    private String next = null;

    private OuterSentenceBreakerIterator(SentenceDetectorIterator inner) {
      this.inner = inner;
    }

    @Override
    public boolean hasNext() {
      while (inner.hasNext() && next == null) {
        next = inner.next();
        if (next != null) {
          next = next.trim();
          if (StringUtils.isEmpty(next)) {
            next = null;
          }
        }
      }
      return next != null;
    }

    @Override
    public String next() {
      String ret = next;
      next = null;
      return ret;
    }

    @Override
    public void remove() {
    }
  }

  private class SentenceDetectorIterator implements Iterator<String> {

    private Span[] starts;
    private int nextPos;
    private String text;
    private boolean stopIteration;

    private SentenceDetectorIterator(String text) {
      this.text = text;
      starts = getStarts(text);
      nextPos = 0;
      stopIteration = false;
    }

    @Override
    public boolean hasNext() {
      return nextPos < starts.length || (nextPos == starts.length && !stopIteration);
    }

    @Override
    public String next() {
      if (nextPos == starts.length && starts[nextPos - 1].getStart() < text.length()) {
        String sent = text.substring(starts[nextPos - 1].getStart());
        stopIteration = true;
        return sent;
      } else if (nextPos >= starts.length) {
        return null;
      } else {
        int start = nextPos == 0
          ? 0 : starts[nextPos - 1].getStart();
        int end = starts[nextPos].getStart();
        String sent = text.substring(start, end);
        nextPos += 1;
        return sent;
      }
    }

    @Override
    public void remove() {
      // no-op
    }

  }

  public static void main(String[] args) throws IOException {
    SentenceBreaker breaker = new SentenceBreaker();

    dumpSentences(breaker, TEST0);
    dumpSentences(breaker, TEST1);
    dumpSentences(breaker, TEST2);
    dumpSentences(breaker, TEST3);
    dumpSentences(breaker, TEST4);

    dumpSentencesFromFile(breaker, "./resources/text-documents/text-document-2.txt");
  }

  private static void dumpSentences(SentenceBreaker breaker, String input) {
    System.out.println("\n>> INPUT: [" + input + "]");
    System.out.println(">> SENTENCES:");

    for (Iterator<String> iter = breaker.iterator(input); iter.hasNext();) {
      final String sentence = iter.next().trim();
      System.out.println("    >> [" + sentence + "]");
    }
  }

  private static void dumpSentencesFromFile(SentenceBreaker breaker, String testFilePath) throws IOException {
    System.out.println("\n>> Test file: " + testFilePath);
    System.out.println(">> SENTENCES:");

    FileInputStream fis = new FileInputStream(testFilePath);
    String text = IOUtils.toString(fis);

    for (Iterator<String> iter = breaker.iterator(text); iter.hasNext();) {
      final String sentence = iter.next().trim();
      System.out.println("    >> [" + sentence + "]");
    }
  }
}
