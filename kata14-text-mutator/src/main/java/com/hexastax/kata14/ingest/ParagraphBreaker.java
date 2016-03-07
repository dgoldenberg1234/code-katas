package com.hexastax.kata14.ingest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

/**
 * Breaks text into paragraphs.
 * 
 * @author dgoldenberg
 */
public class ParagraphBreaker implements Iterator<String> {

  private static final String LINE_SEP = System.getProperty("line.separator");

  private BufferedReader reader = null;

  private String currPara = null;
  private StringBuilder para = new StringBuilder();
  private boolean done = false;

  public ParagraphBreaker(InputStream input) throws IOException {
    this.reader = new BufferedReader(new InputStreamReader(input));
  }

  public void close() throws IOException {
    reader.close();
  }

  @Override
  public boolean hasNext() {
    if (!done && para.length() == 0) {
      try {
        String line = null;
        while ((line = reader.readLine()) != null) {
          if (StringUtils.isBlank(line)) {
            if (para.length() > 0) {
              extractParagraph();
              break;
            }
          } else {
            para.append(line).append(LINE_SEP);
          }
        }
        if (line == null) {
          done = true;
          if (para.length() > 0) {
            extractParagraph();
          } else {
            currPara = null;
          }
        }
      } catch (IOException ex) {
        // TODO handle
      }
    }
    return currPara != null;
  }

  private void extractParagraph() {
    currPara = para.toString().trim();
    para.setLength(0);
  }

  @Override
  public String next() {
    String ret = currPara;
    if (done) {
      currPara = null;
    }
    return ret;
  }

  @Override
  public void remove() {
  }

  public static void main(String[] args) throws IOException {
    dumpParagraphs("./resources/paragraphs/para-test-0.txt");
    dumpParagraphs("./resources/paragraphs/para-test-1.txt");
    dumpParagraphs("./resources/paragraphs/para-test-2.txt");
  }

  private static void dumpParagraphs(String testFilePath) throws IOException {
    System.out.println("\n=======================================================================");
    System.out.println("\n>> Test file: " + testFilePath);
    System.out.println(">> PARAGRAPHS:");
    ParagraphBreaker pb = new ParagraphBreaker(new FileInputStream(testFilePath));
    try {
      for (Iterator<String> iter = pb; iter.hasNext();) {
        System.out.println("[" + iter.next() + "]\n");
      }
    } finally {
      pb.close();
    }
    System.out.println("=======================================================================");
  }
}
