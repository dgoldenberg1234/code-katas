package com.hexastax.kata14.build;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.hexastax.kata14.ingest.NonAsciiHandler;
import com.hexastax.kata14.ingest.ParagraphBreaker;
import com.hexastax.kata14.ingest.PunctuationHandler;
import com.hexastax.kata14.ingest.SentenceBreaker;
import com.hexastax.kata14.ingest.SentenceClassifier;
import com.hexastax.kata14.ingest.SentenceType;
import com.hexastax.kata14.ingest.WordExtractor;
import com.hexastax.kata14.ingest.WordListIterator;
import com.hexastax.kata14.model.Corpus;
import com.hexastax.kata14.model.CorpusDocument;
import com.hexastax.kata14.model.Model;
import com.hexastax.kata14.model.Ngram;
import com.hexastax.kata14.model.Paragraph;
import com.hexastax.kata14.model.Sentence;
import com.hexastax.kata14.model.inmemory.InMemoryModel;
import com.hexastax.kata14.util.Kata14Utils;

// - Corpus
// --- Document
// ----- Paragraph
// ------- Sentence
// --------- Ngrams
public class ModelBuilder {

  private String corpusName = null;
  private String corpusFileLocation = null;
  private int ngramCardinality = 0;
  private Model model = null;

  public ModelBuilder(String corpusName, String corpusFileLocation, int ngramCardinality, Model model) throws IOException {
    Kata14Utils.validateCardinality(ngramCardinality);
    
    this.model = model;
    this.corpusName = corpusName;
    this.corpusFileLocation = corpusFileLocation;
    this.ngramCardinality = ngramCardinality;
  }

  public void buildModel() throws IOException {
    Corpus corpus = model.createCorpus(corpusName, corpusFileLocation);
    while (corpus.hasNext()) {
      CorpusDocument doc = corpus.next();
      processDocument(corpus, doc);
    }
    corpus.close();
  }

  private void processDocument(Corpus corpus, CorpusDocument doc) throws IOException {
    model.createDocument(corpus, doc);

    InputStream stream = doc.getStream();

    ParagraphBreaker pb = new ParagraphBreaker(stream);
    SentenceBreaker sb = new SentenceBreaker();

    try {
      for (int paraNum = 1; pb.hasNext(); paraNum++) {
        String paragraph = pb.next();
        processParagraph(doc, paraNum, sb, paragraph);
      }
    } finally {
      pb.close();
    }
  }

  private void processParagraph(CorpusDocument doc, int paraNum, SentenceBreaker sb, String paragraph) throws IOException {
    Paragraph para = model.createParagraph(doc, paraNum);

    int senNum = 1;
    for (Iterator<String> senIter = sb.iterator(paragraph); senIter.hasNext();) {
      String sentence = senIter.next();
      processSentence(para, senNum, sentence);
      senNum++;
    }
  }

  private void processSentence(Paragraph para, int senNum, String sentence) throws IOException {
    SentenceType sType = SentenceClassifier.getSentenceType(sentence);
    
    Sentence sen = new Sentence(para, sType, senNum);

    sentence = PunctuationHandler.removePunctuation(sentence);
    sentence = NonAsciiHandler.removeNonAscii(sentence);
    List<String> words = WordExtractor.getWords(sentence);

    if (!words.isEmpty()) {
      int count = 0;
      for (WordListIterator wlIter = new WordListIterator(words, ngramCardinality); wlIter.hasNext();) {
        Ngram ngram = wlIter.next();
        ngram.setId(String.format("%d:%s", count + 1, sen.getId()).substring(0, 12));
        model.createNgram(sen, ngram);
        count++;
      }

      // May not have ngrams for very short sentences. If so, don't bother storing an extra
      // sentence identifier.
      if (count > 0) {
        model.createSentence(para, sen);
      }
    }
  }

  public static void main(String[] args) throws IOException {
    Model model = new InMemoryModel();
    ModelBuilder mb = new ModelBuilder("test corpus", "./resources/corpora/test-corpus-0.zip", 4, model);
    mb.buildModel();
    System.out.println(">> MODEL:");
    model.dump("test corpus", System.out);
    model.close();
  }

}
