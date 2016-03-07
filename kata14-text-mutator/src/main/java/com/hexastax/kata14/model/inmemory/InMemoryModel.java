package com.hexastax.kata14.model.inmemory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hexastax.kata14.ingest.SentenceType;
import com.hexastax.kata14.model.Corpus;
import com.hexastax.kata14.model.CorpusDocument;
import com.hexastax.kata14.model.Key;
import com.hexastax.kata14.model.Model;
import com.hexastax.kata14.model.Ngram;
import com.hexastax.kata14.model.Paragraph;
import com.hexastax.kata14.model.Sentence;
import com.hexastax.kata14.model.Table;

/**
 * Represents an in-memory text model.
 * 
 * @author dgoldenberg
 */
public class InMemoryModel extends LinkedHashMap<String, Table>implements Model {

  private static final long serialVersionUID = 4581560004667369748L;

  public InMemoryModel() {
  }

  @Override
  public void initialize() throws IOException {
    put(TABLE_CORPORA, new Table(TABLE_CORPORA));
    put(TABLE_DOCUMENTS, new Table(TABLE_DOCUMENTS));
    put(TABLE_PARAGRAPHS, new Table(TABLE_PARAGRAPHS));
    put(TABLE_SENTENCES, new Table(TABLE_SENTENCES));
    put(TABLE_NGRAMS, new Table(TABLE_NGRAMS));
  }

  public Corpus createCorpus(String corpusName, String corpusFileLocation) throws IOException {
    Corpus corpus = new Corpus(corpusName, corpusFileLocation);
    get(TABLE_CORPORA).put(new Key(corpusName, corpusFileLocation), corpus.getId());
    return corpus;
  }

  public void createDocument(Corpus corpus, CorpusDocument doc) throws IOException {
    get(TABLE_DOCUMENTS).put(new Key(corpus.getId(), doc.getName()), doc.getId());
  }

  public Paragraph createParagraph(CorpusDocument doc, int paraNum) throws IOException {
    Paragraph para = new Paragraph(doc, paraNum);
    get(TABLE_PARAGRAPHS).put(new Key(doc.getId(), Integer.toString(paraNum)), para.getId());
    return para;
  }

  public void createNgram(Sentence sen, Ngram ngram) throws IOException {
    get(TABLE_NGRAMS).put(new Key(sen.getId(), ngram.getFirstAsSummary()), ngram.getSecond());
  }

  public void createSentence(Paragraph para, Sentence sen) throws IOException {
    get(TABLE_SENTENCES).put(new Key(para.getId(), sen.getId()), Integer.toString(sen.getType().getType()));
  }

  public Corpus getCorpus(String name) throws IOException {
    Corpus c = null;

    Table tCorpora = get(TABLE_CORPORA);
    for (Map.Entry<Key, String> entry : tCorpora.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();
      if (key.getFamily().equals(name)) {
        c = new Corpus(value, name, key.getQualifier());
        break;
      }
    }

    return c;
  }

  public List<CorpusDocument> getDocuments(Corpus corpus) throws IOException {
    List<CorpusDocument> docs = new ArrayList<CorpusDocument>();

    Table tDocuments = get(TABLE_DOCUMENTS);
    for (Map.Entry<Key, String> entry : tDocuments.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();
      if (key.getFamily().equals(corpus.getId())) {
        CorpusDocument doc = new CorpusDocument(value, key.getQualifier());
        docs.add(doc);
      }
    }

    return docs;
  }

  public List<Paragraph> getParagraphs(CorpusDocument doc) throws IOException {
    List<Paragraph> paragraphs = new ArrayList<Paragraph>();

    Table tDocuments = get(TABLE_PARAGRAPHS);
    for (Map.Entry<Key, String> entry : tDocuments.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();
      if (key.getFamily().equals(doc.getId())) {
        Paragraph para = new Paragraph(value, Integer.parseInt(key.getQualifier()));
        paragraphs.add(para);
      }
    }

    return paragraphs;
  }

  public List<Sentence> getSentences(Paragraph para) throws IOException {
    List<Sentence> sentences = new ArrayList<Sentence>();

    Table tSentences = get(TABLE_SENTENCES);
    for (Map.Entry<Key, String> entry : tSentences.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();
      if (key.getFamily().equals(para.getId())) {
        Sentence sen = new Sentence(key.getQualifier(), SentenceType.fromTypeValue(Integer.parseInt(value)));
        sentences.add(sen);
      }
    }

    return sentences;
  }

  public List<Ngram> getNgrams(Sentence sen) throws IOException {
    List<Ngram> ngrams = new ArrayList<Ngram>();

    Table tNgrams = get(TABLE_NGRAMS);
    for (Map.Entry<Key, String> entry : tNgrams.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();
      if (key.getFamily().equals(sen.getId())) {
        String[] firstBits = key.getQualifier().split(":");
        List<String> bits = new ArrayList<String>();
        for (String firstBit : firstBits) {
          bits.add(firstBit);
        }
        Ngram ngram = new Ngram(bits, value);
        ngrams.add(ngram);
      }
    }

    return ngrams;
  }

  public List<String> getMatches(Ngram ngram) throws IOException {
    List<String> matches = new ArrayList<String>();

    String summ = ngram.getFirstAsSummary();

    Table tNgrams = get(TABLE_NGRAMS);
    for (Map.Entry<Key, String> entry : tNgrams.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();

      // TODO may need to narrow scope to specific corpus
      if (summ.equals(key.getQualifier())) {
        matches.add(value);
      }
    }

    return matches;
  }

  public Map<String, Double> getMatchesWithWeights(Ngram ngram) throws IOException {
    Map<String, Double> matches = new HashMap<String, Double>();

    String summ = ngram.getFirstAsSummary();

    Table tNgrams = get(TABLE_NGRAMS);
    for (Map.Entry<Key, String> entry : tNgrams.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();

      // TODO may need to narrow scope to specific corpus
      if (summ.equals(key.getQualifier())) {
        Double freq = matches.get(value);
        if (freq == null) {
          matches.put(value, 1d);
        } else {
          matches.put(value, freq + 1);
        }
      }
    }

    return matches;
  }

  public void dump(String corpusName, OutputStream os) throws IOException {
    Table tCorpora = get(TABLE_CORPORA);
    Table tDocuments = get(TABLE_DOCUMENTS);
    Table tParagraphs = get(TABLE_PARAGRAPHS);
    Table tSentences = get(TABLE_SENTENCES);
    Table tNgrams = get(TABLE_NGRAMS);

    for (Map.Entry<Key, String> corpus : tCorpora.entrySet()) {
      corpusName = corpus.getKey().getFamily();
      String corpusFile = corpus.getKey().getQualifier();
      String corpusId = corpus.getValue();
      os.write(("-------- START CORPUS: name=" + corpusName + ", file=" + corpusFile + ", ID=" + corpusId + " --------\n").getBytes());

      List<Map.Entry<Key, String>> documents = tDocuments.getFamily(corpusId);
      for (Map.Entry<Key, String> doc : documents) {
        String docName = doc.getKey().getQualifier();
        String docId = doc.getValue();
        os.write(("  -------- START DOCUMENT: name=" + docName + ", ID=" + docId + " --------\n").getBytes());

        List<Map.Entry<Key, String>> paragraphs = tParagraphs.getFamily(docId);
        for (Map.Entry<Key, String> para : paragraphs) {
          String paraNum = para.getKey().getQualifier();
          String paraId = para.getValue();

          os.write(("    -------- START PARA: num=" + paraNum + ", ID=" + paraId + " --------\n").getBytes());

          List<Map.Entry<Key, String>> sentences = tSentences.getFamily(paraId);
          for (Map.Entry<Key, String> sent : sentences) {
            String sentId = sent.getKey().getQualifier();
            String sentType = sent.getValue();

            os.write(("      -------- START SENT: ID=" + sentId + ", type=" + sentType + " --------\n").getBytes());

            List<Map.Entry<Key, String>> ngrams = tNgrams.getFamily(sentId);
            for (Map.Entry<Key, String> ngram : ngrams) {
              String tuple = ngram.getKey().getQualifier();
              String last = ngram.getValue();

              os.write(("        -------- NGRAM: tuple=" + tuple + ", last=" + last + " --------\n").getBytes());
            }

            os.write(("      -------- END SENT: num=" + sentId + ", type=" + sentType + " ---------\n").getBytes());
          }

          os.write(("    --------- END PARA: name=" + paraNum + ", ID=" + paraId + " ---------\n").getBytes());

        }

        os.write(("  --------- END DOCUMENT: name=" + docName + ", ID=" + docId + " ---------\n").getBytes());
      }

      os.write(("--------- END CORPUS: " + corpusName + " : " + corpusFile + " : " + corpusId + "---------\n").getBytes());
    }

  }

  @Override
  public void close() throws IOException {
    // clear();
  }

}
