package com.hexastax.katas.commons.cmdline;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hexastax.katas.commons.exception.ConfigurationException;
import com.hexastax.katas.commons.util.CodeKatasStringUtils;

/**
 * Represents a command-line tool template (a handy template based on commons-cli).
 * 
 * @author dgoldenberg
 */
@SuppressWarnings("static-access")
public class CliToolTemplate {

  private static final Logger log = LogManager.getLogger(CliToolTemplate.class.getName());
  
  public static final String PARAM_HELP_HELP = "help";
  public static final String PARAM_HELP_QUES = "?";
  public static final String PARAM_HELP_H = "h";

  private static final String PRINT_THIS_MESSAGE = "Print this message.";

  private String cliToolClassName;
  private Options options = new Options();
  // This provides more control for checking than the OptionBuilder.isRequired method in commons
  // cli. If using that method, we can't allow, e.g. -h to be passed in or we get
  // MissingOptionException.
  private Set<String> mandatoryOptions = new LinkedHashSet<String>();
  private CommandLine cmdLine;

  private CliToolTemplate(String cliToolClassName) {
    this.cliToolClassName = cliToolClassName;

    addOption(PARAM_HELP_HELP, PRINT_THIS_MESSAGE);
    addOption(PARAM_HELP_QUES, PRINT_THIS_MESSAGE);
    addOption(PARAM_HELP_H, PRINT_THIS_MESSAGE);
  }

  public static CliToolTemplate create(String utilityClassName) {
    return new CliToolTemplate(utilityClassName);
  }

  public CliToolTemplate withMandatoryOption(String argName, String optName, String description) {
    addArgOption(argName, optName, description);
    mandatoryOptions.add(optName);
    return this;
  }

  public CliToolTemplate withArgOption(String argName, String optName, String description) {
    addArgOption(argName, optName, description);
    return this;
  }

  public CliToolTemplate withOption(String optName, String description) {
    addOption(optName, description);
    return this;
  }

  public CliToolTemplate processArgs(String[] args) throws ConfigurationException {
    // Create the parser.
    CommandLineParser parser = new GnuParser();

    // Parse the command line arguments.
    try {
      cmdLine = parser.parse(options, args);
    } catch (ParseException ex) {
      throw new ConfigurationException("Error while parsing arguments.", ex);
    }

    // Check if we're being asked for the usage.
    if (isBeingAskedForHelp(cmdLine)) {
      usage();
    }

    for (String mandatoryOption : mandatoryOptions) {
      checkMandatoryOption(cmdLine, mandatoryOption);
    }

    return this;
  }

  private boolean isBeingAskedForHelp(CommandLine cmdLine) {
    return cmdLine.hasOption(PARAM_HELP_HELP)
      || cmdLine.hasOption(PARAM_HELP_QUES)
      || cmdLine.hasOption(PARAM_HELP_H);
  }

  private void usage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(100, cliToolClassName, "OPTIONS:", options, null, true);
    System.exit(1);
  }

  private void addOption(String optName, String description) {
    Option opt = OptionBuilder.withDescription(description).create(optName);
    options.addOption(opt);
  }

  private void addArgOption(String argName, String optName, String description) {
    Option option = OptionBuilder.withArgName(argName)
      .hasArg()
      .withDescription(description)
      .create(optName);
    options.addOption(option);
  }

  private static void checkMandatoryOption(CommandLine cmdLine, String option) throws ConfigurationException {
    if (!cmdLine.hasOption(option)) {
      String msg = String.format("Unable to start: no '%s' was provided in configuration.", option);
      throw new ConfigurationException(msg);
    }
  }

  public boolean getBooleanOptionValue(String opt) {
    return cmdLine.hasOption(opt);
  }

  public int getIntOptionValue(String opt, int defaultValue) {
    return Integer.parseInt(getOptionValue(opt, String.valueOf(defaultValue)));
  }

  public int getIntOptionValue(String opt) {
    return Integer.parseInt(getOptionValue(opt));
  }

  public String getOptionValue(String opt, String defaultValue) {
    return cmdLine.getOptionValue(opt, defaultValue);
  }

  public String getOptionValue(String opt) {
    return cmdLine.getOptionValue(opt);
  }
  
  @SuppressWarnings("unchecked")
  public List<String> getOptionValues(String opt) {
    String[] values = cmdLine.getOptionValues(opt);
    return (values == null) ? Collections.EMPTY_LIST : Arrays.asList(values);
  }
  
  public void printExecutionSummary(String job, long startTime, boolean halted, boolean error) {
    long execTime = System.currentTimeMillis() - startTime;
    log.info(">>");
    log.info(">> --------------------------------------------------------------");
    log.info(">>");
    if (halted) {
      log.info(">> Utility '{}' was halted.", job);
    } else if (error) {
      log.info(">> Utility '{}' failed.", job);
    } else {
      log.info(">> Utility '{}' completed.", job);
    }
    if (execTime < 1000) {
      log.info(">> Execution time: {} ms.", execTime);
    } else {
      log.info(">> Execution time: {} ms ({}).", execTime, CodeKatasStringUtils.millisecondsToString(execTime));
    }
  }
}
