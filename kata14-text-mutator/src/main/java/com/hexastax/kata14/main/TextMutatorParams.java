package com.hexastax.kata14.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hexastax.katas.commons.cmdline.CliToolTemplate;

/**
 * Holds the parameters for the Text Mutator.
 * 
 * @author dgoldenberg
 */
public class TextMutatorParams {

  private static final Logger log = LogManager.getLogger(TextMutatorParams.class.getName());

  public static final String PARAM_CORPUS_NAME = "corpName";
  public static final String PARAM_CORPUS_FILE_LOCATION = "corpFileLoc";
  public static final String PARAM_MAX_SENTENCE_LENGTH = "maxSentLen";
  public static final String PARAM_MAX_NUM_PARAGRAPHS = "maxNumPars";
  public static final String PARAM_MAX_NUM_SENTENCES_PER_PARA = "maxNumSentsPerPara";
  
  public static final String PARAM_NGRAM_CARDINALITY = "ngramCard";
  public static final String PARAM_MODEL_TYPE = "modelType";
  
  private String corpusName;
  private String corpusFileLocation;
  private int maxSentenceLength;
  private int maxNumParagraphs;
  private int maxNumSentencesPerParagraph;
  private int ngramCardinality;
  private String modelType;

  private TextMutatorParams() {
  }
  
  public static CliToolTemplate populate(CliToolTemplate cliTemplate) {
    return cliTemplate.withMandatoryOption(
      "corpus name",
      TextMutatorParams.PARAM_CORPUS_NAME,
      "[Required]. The name of the document corpus to use. Provide a user-friendly, unique name for the corpus.")
      .withMandatoryOption(
        "corpus file location",
        TextMutatorParams.PARAM_CORPUS_FILE_LOCATION,
        "[Required]. The path to the corpus directory on disk.")
      .withMandatoryOption(
        "max. sentence length",
        TextMutatorParams.PARAM_MAX_SENTENCE_LENGTH,
        "[Required]. The maximum sentence length (positive integer).")
      .withMandatoryOption(
        "max. total num. paragraphs",
        TextMutatorParams.PARAM_MAX_NUM_PARAGRAPHS,
        "[Required]. The maximum number of paragraphs to generate (positive integer).")
      .withMandatoryOption(
        "max. num. sentences per paragraph",
        TextMutatorParams.PARAM_MAX_NUM_SENTENCES_PER_PARA,
        "[Required]. The maximum number of sentences per paragraph to generate (positive integer).")
      .withMandatoryOption(
        "N-gram cardinality",
        TextMutatorParams.PARAM_NGRAM_CARDINALITY,
        "[Required]. The N-gram cardinality/size (positive integer >= 2)")
      .withMandatoryOption(
        "ID field name",
        TextMutatorParams.PARAM_MODEL_TYPE,
        "[Required]. The type of model to use: inmemory or accumulo.");

  }

  /**
   * Extracts the parameters from the command line.
   * 
   * @param cmdLine
   *          the command line
   * @return the extracted parameters
   */
  public static TextMutatorParams fromCommandLine(CliToolTemplate cmdLine) {
    TextMutatorParams params = new TextMutatorParams();

    params.setCorpusFileLocation(cmdLine.getOptionValue(PARAM_CORPUS_FILE_LOCATION));
    params.setCorpusName(cmdLine.getOptionValue(PARAM_CORPUS_NAME));
    params.setMaxNumParagraphs(cmdLine.getIntOptionValue(PARAM_MAX_NUM_PARAGRAPHS));
    params.setMaxNumSentencesPerParagraph(cmdLine.getIntOptionValue(PARAM_MAX_NUM_SENTENCES_PER_PARA));
    params.setMaxSentenceLength(cmdLine.getIntOptionValue(PARAM_MAX_SENTENCE_LENGTH));
    params.setModelType(cmdLine.getOptionValue(PARAM_MODEL_TYPE));
    params.setNgramCardinality(cmdLine.getIntOptionValue(PARAM_NGRAM_CARDINALITY));

    return params;
  }

  public void dumpToLog() {
    log.info("  >> corpus name                      : {}", corpusName);
    log.info("  >> corpus file location             : {}", corpusFileLocation);
    log.info("  >> max.sentence length              : {}", maxSentenceLength);
    log.info("  >> max.num paragraphs               : {}", maxNumParagraphs);
    log.info("  >> max.num sentences per paragraph  : {}", maxNumSentencesPerParagraph);
    log.info("  >> N-gram cardinality               : {}", ngramCardinality);
    log.info("  >> model type                       : {}", modelType);
  }

  public String getCorpusName() {
    return corpusName;
  }

  public void setCorpusName(String corpusName) {
    this.corpusName = corpusName;
  }

  public String getCorpusFileLocation() {
    return corpusFileLocation;
  }

  public void setCorpusFileLocation(String corpusFileLocation) {
    this.corpusFileLocation = corpusFileLocation;
  }

  public int getMaxSentenceLength() {
    return maxSentenceLength;
  }

  public void setMaxSentenceLength(int maxSentenceLength) {
    this.maxSentenceLength = maxSentenceLength;
  }

  public int getMaxNumParagraphs() {
    return maxNumParagraphs;
  }

  public void setMaxNumParagraphs(int maxNumParagraphs) {
    this.maxNumParagraphs = maxNumParagraphs;
  }

  public int getMaxNumSentencesPerParagraph() {
    return maxNumSentencesPerParagraph;
  }

  public void setMaxNumSentencesPerParagraph(int maxNumSentencesPerParagraph) {
    this.maxNumSentencesPerParagraph = maxNumSentencesPerParagraph;
  }

  public int getNgramCardinality() {
    return ngramCardinality;
  }

  public void setNgramCardinality(int ngramCardinality) {
    this.ngramCardinality = ngramCardinality;
  }

  public String getModelType() {
    return modelType;
  }

  public void setModelType(String modelType) {
    this.modelType = modelType;
  }

}
