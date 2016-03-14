package com.hexastax.kata04.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Represents a simple refactoring exercise.
 * 
 * @author dgoldenberg
 */
public class DataMungingApp {

  public DataMungingApp() {
  }

  /**
   * Finds minimum difference between two values identified by their indexes within a row.
   * 
   * @param inStream
   *          the data input stream
   * @param outputValueIdx
   *          the index of the column value that needs to be returned for the row with the minimum
   *          difference calculated
   * @param val1Idx
   *          the index value for the first value to use for calculating the difference
   * @param val2Idx
   *          the index value for the second value to use for calculating the difference
   * @param rowMustHaveVals
   *          if this is not equal to -1, then a check must be made for whether a given row contains
   *          the expected number of cells (e.g. to skip a row with a custom separator)
   * @return a pair of { integer value, string value } where the integer is the minimum difference
   *         and the string is the output string value for the row with the minimum difference
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Pair<Integer, String> findMinDiff(InputStream inStream, int outputValueIdx, int val1Idx, int val2Idx, int rowMustHaveVals) {
    Pair<Integer, String> result = null;

    try (Scanner scan = new Scanner(inStream)) {
      boolean isFirstLine = true;

      String output = null;
      int minDiff = Integer.MAX_VALUE;

      // For each line
      while (scan.hasNextLine()) {
        String line = scan.nextLine().trim();

        // Skip the first (header) line TODO make this configurable
        if (isFirstLine) {
          isFirstLine = false;
        }
        // Skip empty/blank lines
        else if (StringUtils.isNotEmpty(line)) {
          // Pull out the column values
          String[] colValues = line.split("\\s+");

          // Optionally, validate that the number of column values is as expected
          if (rowMustHaveVals != -1 && colValues.length != rowMustHaveVals) {
            continue;
          }

          // Calculate the difference of specified values; keep track of the minimum value.
          try {
            String sOne = colValues[val1Idx];
            String sTwo = colValues[val2Idx];

            int one = getCleanIntVal(sOne);
            int two = getCleanIntVal(sTwo);
            int diff = Math.abs(one - two);
            if (diff < minDiff) {
              output = colValues[outputValueIdx];
              minDiff = diff;
            }
          } catch (NumberFormatException ex) {
          }
        }
      }
      result = new ImmutablePair(minDiff, output);

    } catch (Exception ex) {
      // TODO log or rethrow
      ex.printStackTrace();
    }

    return result;
  }

  private static int getCleanIntVal(String sVal) {
    if (sVal.endsWith("*")) {
      sVal = sVal.substring(0, sVal.length() - 1);
    }
    return Integer.parseInt(sVal);
  }

  public static void main(String[] args) throws IOException {
    // Test with the weather data
    Pair<Integer, String> result = new DataMungingApp().findMinDiff(
      DataMungingApp.class.getResourceAsStream("/weather.dat"), 0, 1, 2, -1);
    if (result != null) {
      System.out.println(">> Min temp spread: " + result.getLeft());
      System.out.println(">> Day: " + result.getRight());
    } else {
      System.out.println(">> Unable to calculate min diff for weather.");
    }

    System.out.println();

    // Test with the football data
    result = new DataMungingApp().findMinDiff(
      DataMungingApp.class.getResourceAsStream("/football.dat"), 1, 6, 8, 10);
    if (result != null) {
      System.out.println(">> Min diff: " + result.getLeft());
      System.out.println(">> Team: " + result.getRight());
    } else {
      System.out.println(">> Unable to calculate min diff for football.");
    }
  }
}
