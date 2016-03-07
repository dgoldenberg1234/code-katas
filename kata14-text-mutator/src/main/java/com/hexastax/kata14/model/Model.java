package com.hexastax.kata14.model;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Represents a model generated from given text.
 * 
 * @author dgoldenberg
 */
public interface Model {

  static final String TABLE_CORPORA = "Corpora";
  static final String TABLE_DOCUMENTS = "Documents";
  static final String TABLE_PARAGRAPHS = "Paragraphs";
  static final String TABLE_SENTENCES = "Sentences";
  static final String TABLE_NGRAMS = "Ngrams";

  void initialize() throws IOException;

  Corpus createCorpus(String corpusName, String corpusFileLocation) throws IOException;

  void createDocument(Corpus corpus, CorpusDocument doc) throws IOException;

  Paragraph createParagraph(CorpusDocument doc, int paraNum) throws IOException;

  void createNgram(Sentence sen, Ngram ngram) throws IOException;

  void createSentence(Paragraph para, Sentence sen) throws IOException;

  Corpus getCorpus(String name) throws IOException;

  List<CorpusDocument> getDocuments(Corpus corpus) throws IOException;

  List<Paragraph> getParagraphs(CorpusDocument doc) throws IOException;

  List<Sentence> getSentences(Paragraph para) throws IOException;

  List<Ngram> getNgrams(Sentence sen) throws IOException;

  List<String> getMatches(Ngram ngram) throws IOException;

  Map<String, Double> getMatchesWithWeights(Ngram ngram) throws IOException;

  void dump(String corpusName, OutputStream os) throws IOException;

  void close() throws IOException;
}
