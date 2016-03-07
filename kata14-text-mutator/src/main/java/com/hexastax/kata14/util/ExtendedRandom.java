package com.hexastax.kata14.util;

import java.util.Map;
import java.util.Random;

/**
 * Provides a few extra capabilities on top of the standard randomizer.
 * 
 * @author dgoldenberg
 */
public class ExtendedRandom extends Random {

  private static final long serialVersionUID = -2337421628151536659L;

  public ExtendedRandom() {
  }

  /**
   * Generates a random non-zero integer.
   * 
   * @param rand
   * @param n
   * @return
   */
  public int getRandNonZero(Random rand, int n) {
    int r = 0;
    if (n > 0) {
      while (r == 0) {
        r = rand.nextInt(n);
      }
    }
    return r;
  }

  /**
   * Picks a random item out of a set of weighted items.
   * 
   * @param weights
   * @return
   * @see http://stackoverflow.com/questions/6737283/weighted-randomness-in-java
   */
  public <E> E getWeightedRandom(Map<E, Double> weights) {
    E result = null;
    double bestValue = Double.MAX_VALUE;

    for (E element : weights.keySet()) {
      double value = -Math.log(nextDouble()) / weights.get(element);

      if (value < bestValue) {
        bestValue = value;
        result = element;
      }
    }

    return result;
  }
}
