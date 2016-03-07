package com.hexastax.kata14.main;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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

/**
 * Implements the Kata 14 from here: http://codekata.pragprog.com/2007/01/kata_fourteen_t.html.
 * <p>
 * Reads in a specific text document, extracts N-grams of configurable cardinality and allows the
 * user to create a new, mutated text based on the N-grams.
 * 
 * @author dgoldenberg
 */
public class TextMutator {

  private static final String LINE_SEP = System.getProperty("line.separator");

  private String corpusName = null;
  private int maxSentenceLength = 0;
  private int maxNumParagraphs = 0;
  private int maxNumSentencesPerParagraph = 0;
  private int ngramCardinality = 0;
  private Model model = null;
  private ExtendedRandom rand = null;

  /**
   * Creates the text mutator.
   * 
   * @param corpusName
   *          the name of the text corpus
   * @param model
   *          the text model
   * @param maxSentenceLength
   *          the maximum sentence length for generated text
   * @param maxNumParagraphs
   *          the maximum number of paragraphs to generate
   * @param maxNumSentencesPerParagraph
   *          the maximum number of sentences per paragraph
   * @param ngramCardinality
   *          the N-gram cardinality e.g. 2
   */
  public TextMutator(
    String corpusName,
    Model model,
    int maxSentenceLength,
    int maxNumParagraphs,
    int maxNumSentencesPerParagraph,
    int ngramCardinality) {

    Kata14Utils.validateCardinality(ngramCardinality);

    this.corpusName = corpusName;
    this.model = model;
    this.maxSentenceLength = maxSentenceLength;
    this.maxNumParagraphs = maxNumParagraphs;
    this.maxNumSentencesPerParagraph = maxNumSentencesPerParagraph;
    this.ngramCardinality = ngramCardinality;
    this.rand = new ExtendedRandom();
  }

  /**
   * Generates a text mutation.
   * 
   * @param out
   *          the output stream to write to
   * @throws IOException
   */
  public void generateMutation(OutputStream out) throws IOException {
    int paraCount = 0;

    Corpus corpus = model.getCorpus(corpusName);

    // TODO don't retrieve full set of docs here, just the randomly picked one in a loop
    List<CorpusDocument> docs = model.getDocuments(corpus);

    while (paraCount < maxNumParagraphs) {
      mutateParagraph(out, docs);
      out.write(LINE_SEP.getBytes());
      out.write(LINE_SEP.getBytes());
      paraCount++;
    }
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

  public static void main(String[] args) throws Exception {
    final String corpusName = args[0];
    final String corpusFileLocation = args[1];
    final int maxSentenceLength = Integer.parseInt(args[2]);
    final int maxNumParagraphs = Integer.parseInt(args[3]);
    final int maxNumSentencesPerParagraph = Integer.parseInt(args[4]);
    final int ngramCardinality = Integer.parseInt(args[5]);
    final String modelType = args[6];

    Model model = ModelFactory.getModel(modelType);
    model.initialize();

    ModelBuilder builder = new ModelBuilder(corpusName, corpusFileLocation, ngramCardinality, model);
    builder.buildModel();

    // model.dump(corpusName, System.out);
    // System.out.println("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");

    model.close();

    TextMutator mutator = new TextMutator(corpusName, model, maxSentenceLength, maxNumParagraphs, maxNumSentencesPerParagraph, ngramCardinality);
    mutator.generateMutation(System.out);
  }
}
