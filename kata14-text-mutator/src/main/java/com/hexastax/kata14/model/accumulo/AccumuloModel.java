package com.hexastax.kata14.model.accumulo;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.accumulo.core.cli.BatchWriterOpts;
import org.apache.accumulo.core.cli.ClientOnRequiredTable;
import org.apache.accumulo.core.cli.ScannerOpts;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MultiTableBatchWriter;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Mutation;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.accumulo.examples.simple.helloworld.InsertWithBatchWriter;
import org.apache.hadoop.io.Text;

import com.beust.jcommander.Parameter;
import com.hexastax.kata14.ingest.SentenceType;
import com.hexastax.kata14.model.Corpus;
import com.hexastax.kata14.model.CorpusDocument;
import com.hexastax.kata14.model.Model;
import com.hexastax.kata14.model.Ngram;
import com.hexastax.kata14.model.Paragraph;
import com.hexastax.kata14.model.Sentence;

/**
 * Represents a model stored in Accumulo.
 * 
 * @author dgoldenberg
 */
public class AccumuloModel implements Model {

  private ClientOnRequiredTable writeOpts = null;
  private Connector writeConnector = null;
  private MultiTableBatchWriter mtbw = null;

  private Opts readOpts = null;
  private Connector readConnector = null;

  public AccumuloModel(String[] args) throws AccumuloException, AccumuloSecurityException {
    // Get a write connector
    writeOpts = new ClientOnRequiredTable();
    BatchWriterOpts bwOpts = new BatchWriterOpts();
    writeOpts.parseArgs(InsertWithBatchWriter.class.getName(), args, bwOpts);
    writeConnector = writeOpts.getConnector();
    mtbw = writeConnector.createMultiTableBatchWriter(bwOpts.getBatchWriterConfig());

    // Get a read connector
    readOpts = new Opts();
    ScannerOpts scanOpts = new ScannerOpts();
    readOpts.parseArgs(this.getClass().getName(), args, scanOpts);
    readConnector = readOpts.getConnector();
  }

  @Override
  public void initialize() throws IOException {
    createTable(TABLE_CORPORA);
    createTable(TABLE_DOCUMENTS);
    createTable(TABLE_PARAGRAPHS);
    createTable(TABLE_SENTENCES);
    createTable(TABLE_NGRAMS);
  }

  private void createTable(String tableName) throws IOException {
    if (!writeConnector.tableOperations().exists(tableName)) {
      try {
        writeConnector.tableOperations().create(tableName);
      } catch (Exception ex) {
        throw new IOException("Error while creating table.", ex);
      }
    }
  }

  @Override
  public Corpus createCorpus(String corpusName, String corpusFileLocation) throws IOException {
    Corpus corpus = new Corpus(corpusName, corpusFileLocation);

    try {
      BatchWriter bw = mtbw.getBatchWriter(TABLE_CORPORA);

      Mutation m = new Mutation(new Text(corpus.getId()));

      // family: corpus name
      // qualifier: corpus file location
      // value: corpus ID
      m.put(new Text(corpusName), new Text(corpusFileLocation), new Value(corpus.getId().getBytes()));

      bw.addMutation(m);
    } catch (Exception ex) {
      throw new IOException("Error while persisting corpus.", ex);
    }

    return corpus;
  }

  @Override
  public void createDocument(Corpus corpus, CorpusDocument doc) throws IOException {
    try {
      BatchWriter bw = mtbw.getBatchWriter(TABLE_DOCUMENTS);

      Mutation m = new Mutation(new Text(doc.getId()));

      // family: corpus ID
      // qualifier: document name
      // value: document ID
      m.put(new Text(corpus.getId()), new Text(doc.getName()), new Value(doc.getId().getBytes()));

      bw.addMutation(m);
    } catch (Exception ex) {
      throw new IOException("Error while persisting document.", ex);
    }
  }

  @Override
  public Paragraph createParagraph(CorpusDocument doc, int paraNum) throws IOException {
    Paragraph para = new Paragraph(doc, paraNum);

    try {
      BatchWriter bw = mtbw.getBatchWriter(TABLE_PARAGRAPHS);

      Mutation m = new Mutation(new Text(para.getId()));

      // family: document ID
      // qualifier: paragraph number
      // value: paragraph ID
      m.put(new Text(doc.getId()), new Text(Integer.toString(paraNum)), new Value(para.getId().getBytes()));
      bw.addMutation(m);
    } catch (Exception ex) {
      throw new IOException("Error while persisting paragraph.", ex);
    }

    return para;
  }

  @Override
  public void createNgram(Sentence sen, Ngram ngram) throws IOException {
    try {
      BatchWriter bw = mtbw.getBatchWriter(TABLE_NGRAMS);

      Mutation m = new Mutation(new Text(sen.getId()));

      // family: sentence ID
      // qualifier: the tuple (everything in the ngram except the last part)
      // value: the match (the last part of the ngram)
      m.put(new Text(sen.getId()), new Text(ngram.getFirstAsSummary()), new Value(ngram.getSecond().getBytes()));

      bw.addMutation(m);
    } catch (Exception ex) {
      throw new IOException("Error while persisting ngram.", ex);
    }
  }

  @Override
  public void createSentence(Paragraph para, Sentence sen) throws IOException {
    try {
      BatchWriter bw = mtbw.getBatchWriter(TABLE_SENTENCES);

      Mutation m = new Mutation(new Text(sen.getId()));

      // family: paragraph ID
      // qualifier: sentence ID
      // value: sentence type
      m.put(new Text(para.getId()), new Text(sen.getId()), new Value(Integer.toString(sen.getType().getType()).getBytes()));
      bw.addMutation(m);
    } catch (Exception ex) {
      throw new IOException("Error while persisting sentence.", ex);
    }
  }

  @Override
  public Corpus getCorpus(String name) throws IOException {
    Corpus corpus = null;

    try {
      Scanner scan = readConnector.createScanner(TABLE_CORPORA, readOpts.auths);

      scan.setRange(new Range(name, name));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      if (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();
        Text family = e.getKey().getColumnFamily();
        Text qualifier = e.getKey().getColumnQualifier();
        Value value = e.getValue();

        corpus = new Corpus(value.toString(), family.toString(), qualifier.toString());
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving corpus.", ex);
    }

    return corpus;
  }

  @Override
  public List<CorpusDocument> getDocuments(Corpus corpus) throws IOException {
    List<CorpusDocument> docs = new ArrayList<CorpusDocument>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_DOCUMENTS, readOpts.auths);

      // TODO
      // Key key = new Key(null, corpus.getId(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();
        Text family = e.getKey().getColumnFamily();

        if (corpus.getId().equals(family.toString())) {
          Text qualifier = e.getKey().getColumnQualifier();
          Value value = e.getValue();

          CorpusDocument doc = new CorpusDocument(value.toString(), qualifier.toString());
          docs.add(doc);
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving documents in a corpus.", ex);
    }

    return docs;
  }

  @Override
  public List<Paragraph> getParagraphs(CorpusDocument doc) throws IOException {
    List<Paragraph> paragraphs = new ArrayList<Paragraph>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_PARAGRAPHS, readOpts.auths);

      // TODO
      // Key key = new Key(null, doc.getId(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();
        Text family = e.getKey().getColumnFamily();

        if (doc.getId().equals(family.toString())) {
          Text qualifier = e.getKey().getColumnQualifier();
          Value value = e.getValue();

          Paragraph para = new Paragraph(value.toString(), Integer.parseInt(qualifier.toString()));
          paragraphs.add(para);
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving paragraphs in a document.", ex);
    }

    return paragraphs;
  }

  @Override
  public List<Sentence> getSentences(Paragraph para) throws IOException {
    List<Sentence> sentences = new ArrayList<Sentence>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_SENTENCES, readOpts.auths);

      // TODO
      // Key key = new Key(null, para.getId(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();
        Text family = e.getKey().getColumnFamily();

        if (para.getId().equals(family.toString())) {
          Text qualifier = e.getKey().getColumnQualifier();
          Value value = e.getValue();

          Sentence sen = new Sentence(qualifier.toString(), SentenceType.fromTypeValue(Integer.parseInt(value.toString())));
          sentences.add(sen);
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving sentences in a paragraph.", ex);
    }

    return sentences;
  }

  @Override
  public List<Ngram> getNgrams(Sentence sen) throws IOException {
    List<Ngram> ngrams = new ArrayList<Ngram>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_NGRAMS, readOpts.auths);

      // TODO
      // Key key = new Key(null, sen.getId(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();
        Text family = e.getKey().getColumnFamily();

        if (sen.getId().equals(family.toString())) {
          Text qualifier = e.getKey().getColumnQualifier();
          Value value = e.getValue();

          final String firstSummary = qualifier.toString();
          final String[] bits = firstSummary.split(":");
          List<String> firstBits = new ArrayList<String>();
          for (String bit : bits) {
            firstBits.add(bit);
          }
          Ngram ngram = new Ngram(firstBits, value.toString());
          ngrams.add(ngram);
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving ngrams in a sentence.", ex);
    }

    return ngrams;
  }

  @Override
  public List<String> getMatches(Ngram ngram) throws IOException {
    List<String> matches = new ArrayList<String>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_NGRAMS, readOpts.auths);

      // TODO
      // Key key = new Key(null, ngram.getFirstAsSummary(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();

        Text family = e.getKey().getColumnFamily();
        if (ngram.getFirstAsSummary().equals(family.toString())) {
          Value value = e.getValue();

          matches.add(value.toString());
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving matches for ngram.", ex);
    }

    return matches;
  }

  @Override
  public Map<String, Double> getMatchesWithWeights(Ngram ngram) throws IOException {
    Map<String, Double> matches = new HashMap<String, Double>();

    try {
      Scanner scan = readConnector.createScanner(TABLE_NGRAMS, readOpts.auths);

      // TODO
      // Key key = new Key(null, ngram.getFirstAsSummary(), null);
      // scan.setRange(new Range(key, key));

      Iterator<Entry<Key, Value>> iter = scan.iterator();
      while (iter.hasNext()) {
        Entry<Key, Value> e = iter.next();

        Text qualifier = e.getKey().getColumnQualifier();
        if (ngram.getFirstAsSummary().equals(qualifier.toString())) {
          Value value = e.getValue();

          Double freq = matches.get(value);
          if (freq == null) {
            matches.put(value.toString(), 1d);
          } else {
            matches.put(value.toString(), freq + 1);
          }
        }
      }

      scan.close();
    } catch (Exception ex) {
      throw new IOException("Error while retrieving matches for ngram.", ex);
    }

    return matches;
  }

  @Override
  public void dump(String corpusName, OutputStream os) throws IOException {
    Corpus corpus = getCorpus(corpusName);

    os.write(("-------- START CORPUS: name=" + corpus.getName() + ", file=" + corpus.getFileLocation() + ", ID=" + corpus.getId() + " --------\n").getBytes());

    List<CorpusDocument> docs = getDocuments(corpus);
    for (CorpusDocument doc : docs) {
      os.write(("  -------- START DOCUMENT: name=" + doc.getName() + ", ID=" + doc.getId() + " --------\n").getBytes());

      List<Paragraph> paragraphs = getParagraphs(doc);
      for (Paragraph para : paragraphs) {

        os.write(("    -------- START PARA: num=" + para.getNum() + ", ID=" + para.getId() + " --------\n").getBytes());

        List<Sentence> sentences = getSentences(para);
        for (Sentence sent : sentences) {
          os.write(("      -------- START SENT: ID=" + sent.getId() + ", type=" + sent.getType() + " --------\n").getBytes());

          List<Ngram> ngrams = getNgrams(sent);
          for (Ngram ngram : ngrams) {
            os.write(("        -------- NGRAM: tuple=" + ngram.getFirstAsSummary() + ", last=" + ngram.getSecond() + " --------\n").getBytes());
          }

          os.write(("      -------- END SENT: num=" + sent.getId() + ", type=" + sent.getType() + " ---------\n").getBytes());
        }

        os.write(("    --------- END PARA: name=" + para.getNum() + ", ID=" + para.getId() + " ---------\n").getBytes());

      }

      os.write(("  --------- END DOCUMENT: name=" + doc.getName() + ", ID=" + doc.getId() + " ---------\n").getBytes());
    }

    os.write(("--------- END CORPUS: " + corpus.getName() + ", file=" + corpus.getFileLocation() + ", ID=" + corpus.getId() + "---------\n").getBytes());

  }

  public void dumpAll() throws Exception {
    String[] tables = { TABLE_CORPORA, TABLE_DOCUMENTS, TABLE_PARAGRAPHS, TABLE_SENTENCES, TABLE_NGRAMS };
    for (String table : tables) {
      dumpTable(table);
    }
  }

  private void dumpTable(String tableName) throws TableNotFoundException {
    System.out.println("\n=== START TABLE: " + tableName);
    Scanner scan = readConnector.createScanner(tableName, readOpts.auths);
    Iterator<Entry<Key, Value>> iter = scan.iterator();
    while (iter.hasNext()) {
      Entry<Key, Value> e = iter.next();

      Text row = e.getKey().getRow();
      Text family = e.getKey().getColumnFamily();
      Text qualifier = e.getKey().getColumnQualifier();
      Value value = e.getValue();

      System.out.println();
      System.out.println("      row: [" + row + "]");
      System.out.println("      fam: [" + family + "]");
      System.out.println("      qua: [" + qualifier + "]");
      System.out.println("      val: [" + value + "]");
      System.out.println();

      System.out.println("\n=== END TABLE: " + tableName);
    }
    scan.close();
  }

  @Override
  public void close() throws IOException {
    try {
      mtbw.close();
    } catch (MutationsRejectedException ex) {
      throw new IOException("Error while closing model.", ex);
    }
  }

  static class Opts extends ClientOnRequiredTable {
    @Parameter(names = "--startKey")
    String startKey;
    @Parameter(names = "--endKey")
    String endKey;
  }
}
