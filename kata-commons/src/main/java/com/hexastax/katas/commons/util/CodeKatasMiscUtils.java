package com.hexastax.katas.commons.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Contains miscellaneous utility methods.
 * 
 * @author dgoldenberg
 */
public class CodeKatasMiscUtils {

  private CodeKatasMiscUtils() {
  }

  /**
   * Rounds a double value to specified number of decimal places. For example round(2.1234, 2)
   * returns 2.12.
   * 
   * @param value
   *          the double value
   * @param places
   *          the number of places
   * @return the result
   */
  public static double round(double value, int places) {
    return new BigDecimal(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
  }

  /**
   * Calculates documents per second.
   * 
   * @param numProcessed
   *          the number of processed documents
   * @param execTime
   *          the execution time
   * @return the documents per second speed of processing
   */
  public static double getDocsPerSec(long numProcessed, long execTime) {
    return CodeKatasMiscUtils.round(numProcessed * 1000 / execTime, 2);
  }
}
