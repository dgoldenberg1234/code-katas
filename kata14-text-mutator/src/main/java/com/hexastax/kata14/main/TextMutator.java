package com.hexastax.kata14.main;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hexastax.kata14.build.ModelBuilder;
import com.hexastax.kata14.ingest.SentenceType;
import com.hexastax.kata14.model.Corpus;
import com.hexastax.kata14.model.CorpusDocument;
import com.hexastax.kata14.model.Model;
import com.hexastax.kata14.model.ModelFactory;
import com.hexastax.kata14.model.Ngram;
import com.hexastax.kata14.model.Paragraph;
import com.hexastax.kata14.model.Sentence;
import com.hexastax.kata14.util.ExtendedRandom;
import com.hexastax.kata14.util.Kata14Utils;
import com.hexastax.katas.commons.cmdline.CliToolTemplate;
import com.hexastax.katas.commons.exception.ConfigurationException;
import com.hexastax.katas.commons.exception.PersistenceException;

/**
 * Implements the Kata 14 from here: http://codekata.pragprog.com/2007/01/kata_fourteen_t.html.
 * <p>
 * Reads in a specific text document, extracts N-grams of configurable cardinality and allows the
 * user to create a new, mutated text based on the N-grams.
 * 
 * @author dgoldenberg
 */
public class TextMutator {

  private static final String CLASSNAME = TextMutator.class.getName();
  
  private static final Logger log = LogManager.getLogger(CLASSNAME);

  private static final String LINE_SEP = System.getProperty("line.separator");

  private String corpusName;
  private int maxSentenceLength = 0;
  private int maxNumParagraphs = 0;
  private int maxNumSentencesPerParagraph = 0;
  private int ngramCardinality = 0;
  private Model model;
  private ExtendedRandom rand = new ExtendedRandom();

  private CliToolTemplate cliTemplate;

  public TextMutator() {
    cliTemplate = TextMutatorParams.populate(CliToolTemplate.create(CLASSNAME));
  }

  public void generateMutation(String[] args, OutputStream out) throws IOException, ConfigurationException, PersistenceException {
    // Extract all the needed parameters.
    TextMutatorParams params = TextMutatorParams.fromCommandLine(cliTemplate.processArgs(args));

    log.info(">> Starting text mutation generator...");
    params.dumpToLog();

    this.corpusName = params.getCorpusName();
    String corpusFileLocation = params.getCorpusFileLocation();
    String modelType = params.getModelType();
    this.maxSentenceLength = params.getMaxSentenceLength();
    this.maxNumParagraphs = params.getMaxNumParagraphs();
    this.maxNumSentencesPerParagraph = params.getMaxNumSentencesPerParagraph();
    this.ngramCardinality = params.getNgramCardinality();

    Kata14Utils.validateCardinality(ngramCardinality);

    model = ModelFactory.getModel(modelType);
    model.initialize();

    ModelBuilder builder = new ModelBuilder(corpusName, corpusFileLocation, ngramCardinality, model);
    builder.buildModel();

    // model.dump(corpusName, System.out);
    // System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

    model.close();

    int paraCount = 0;

    Corpus corpus = model.getCorpus(corpusName);

    // TODO don't retrieve full set of docs here, just the randomly picked one in a loop
    List<CorpusDocument> docs = model.getDocuments(corpus);

    out.write(LINE_SEP.getBytes());
    out.write(LINE_SEP.getBytes());
    out.write("=====================".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write(">> START MUTATED TEXT".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write("=====================".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write(LINE_SEP.getBytes());
    
    while (paraCount < maxNumParagraphs) {
      mutateParagraph(out, docs);
      out.write(LINE_SEP.getBytes());
      out.write(LINE_SEP.getBytes());
      paraCount++;
    }

    out.write("===================".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write("<< END MUTATED TEXT".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write("===================".getBytes());
    out.write(LINE_SEP.getBytes());
    out.write(LINE_SEP.getBytes());
  }

  private void mutateParagraph(OutputStream out, List<CorpusDocument> docs) throws IOException {
    // System.out.println(">> PARA");
    final int sentencesToMutate = rand.getRandNonZero(rand, maxNumSentencesPerParagraph);

    // TODO don't retrieve full sets of objects here, just pick single random items
    for (int senCount = 0; senCount < sentencesToMutate; senCount++) {

      int pickIdx = rand.nextInt(docs.size());

      CorpusDocument pickDoc = docs.get(pickIdx);
      List<Paragraph> paragraphs = model.getParagraphs(pickDoc);
      pickIdx = rand.nextInt(paragraphs.size());

      Paragraph pickPara = paragraphs.get(pickIdx);
      List<Sentence> sentences = model.getSentences(pickPara);
      if (!sentences.isEmpty()) {
        pickIdx = rand.nextInt(sentences.size());
        mutateSentence(out, pickIdx, sentences, senCount == 0);
      }
    }
  }

  private void mutateSentence(OutputStream out, int pickIdx, List<Sentence> sentences, boolean firstInPara) throws IOException {

    // System.out.println(">> SEN");
    Sentence pickSen = sentences.get(pickIdx);
    SentenceType senType = pickSen.getType();

    List<Ngram> ngrams = model.getNgrams(pickSen);

    if (!ngrams.isEmpty()) {
      if (!firstInPara) {
        out.write(' ');
      }
      if (senType.isHandled()) {
        out.write(senType.getSentenceStarter().getBytes());
      }

      final int max = Math.min(ngrams.size(), maxSentenceLength);
      pickIdx = rand.nextInt(ngrams.size());

      Ngram pickNgram = ngrams.get(pickIdx);

      // System.out.println("\n>> max: " + max);
      // System.out.println(">> STARTING WITH NGRAM: " + pickNgram);

      int numEmittedWords = 0;
      Map<String, Double> matches = model.getMatchesWithWeights(pickNgram);
      while (!matches.isEmpty() && numEmittedWords < max) {
        // System.out.println("\n>> MATCHES: " + matches);

        String pickMatch = rand.getWeightedRandom(matches);

        pickNgram.setSecond(pickMatch);
        if (numEmittedWords == 0) {
          printFullNgram(out, pickNgram);
          numEmittedWords += ngramCardinality;
        } else {
          // TODO last ngram tends to have a lot of stopwords e.g. a, the, at, with.
          printNgramEnd(out, pickNgram.getSecond());
          numEmittedWords++;
        }

        List<String> first = pickNgram.getFirst();
        first.remove(0);
        first.add(pickMatch);
        pickNgram.setSecond(null);

        // System.out.println("\n>> NGRAM IS NOW: " + pickNgram);
        // System.out.println("\n>> num emitted: " + numEmittedWords);
        matches = model.getMatchesWithWeights(pickNgram);
      }

      if (senType.isHandled()) {
        out.write(senType.getSentenceCloser().getBytes());
      } else {
        out.write('.');
      }
    }
  }

  private void printFullNgram(OutputStream out, Ngram pickNgram) throws IOException {
    List<String> first = pickNgram.getFirst();

    for (int j = 0; j < first.size(); j++) {
      String f = first.get(j);
      if (j == 0) {
        char fch = f.charAt(0);
        String rest = f.substring(1);
        out.write(Character.toUpperCase(fch));
        out.write(rest.getBytes());
      } else {
        out.write(' ');
        out.write(f.getBytes());
      }
    }
    out.write(' ');
    out.write(pickNgram.getSecond().getBytes());
  }

  private void printNgramEnd(OutputStream out, String end) throws IOException {
    out.write(' ');
    out.write(end.getBytes());
  }

  /**
   * Main.
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    new TextMutator().generateMutation(args, System.out);
    log.info(">> Text mutation generation finished.");
  }
}
