package com.hexastax.kata14.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;

/**
 * Represents a corpus of documents.
 * 
 * @author dgoldenberg
 */
public class Corpus implements Iterator<CorpusDocument> {

  private String id = null;
  private String name = null;
  private String fileLocation = null;
  private ZipInputStream zis = null;
  private CorpusDocument currDoc = null;

  public Corpus(String name, String fileLocation) throws IOException {
    this.name = name;
    this.fileLocation = fileLocation;
    this.id = name;

    InputStream theFile = new FileInputStream(fileLocation);
    zis = new ZipInputStream(theFile);
  }

  public Corpus(String id, String name, String fileLocation) {
    this.id = id;
    this.name = name;
    this.fileLocation = fileLocation;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getFileLocation() {
    return fileLocation;
  }

  @Override
  public boolean hasNext() {
    byte[] buffer = new byte[2048];

    ZipEntry entry;
    try {
      while (currDoc == null && (entry = zis.getNextEntry()) != null) {
        if (!entry.isDirectory()) {
          ByteArrayOutputStream output = null;
          try {
            output = new ByteArrayOutputStream();
            int len = 0;
            while ((len = zis.read(buffer)) > 0) {
              output.write(buffer, 0, len);
            }
          } finally {
            if (output != null)
              output.close();
          }
          currDoc = new CorpusDocument(name, entry.getName(), new ByteArrayInputStream(output.toByteArray()));
        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return currDoc != null;
  }

  @Override
  public CorpusDocument next() {
    CorpusDocument ret = currDoc;
    currDoc = null;
    return ret;
  }

  @Override
  public void remove() {
  }

  public void close() throws IOException {
    zis.close();
  }

  @Override
  public String toString() {
    return "Corpus [id=" + id + ", name=" + name + ", fileLocation=" + fileLocation + "]";
  }

  public static void main(String args[]) throws Exception {
    Corpus c = new Corpus("test-corpus-1", "./resources/corpora/test-corpus-1.zip");
    while (c.hasNext()) {
      CorpusDocument doc = c.next();

      System.out.println("=============================================================");
      System.out.println(">> Corpus doc: " + doc.getName());
      System.out.println("=============================================================\n");
      InputStream is = doc.getStream();
      String content = IOUtils.toString(is, "UTF-8");
      System.out.println(content);
      System.out.println("=============================================================");
      System.out.println("=============================================================\n\n\n");
    }
    c.close();
  }

}
