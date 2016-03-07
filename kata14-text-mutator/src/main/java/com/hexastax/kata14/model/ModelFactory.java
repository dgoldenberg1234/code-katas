package com.hexastax.kata14.model;

import org.apache.commons.lang.StringUtils;

import com.hexastax.kata14.model.accumulo.AccumuloModel;
import com.hexastax.kata14.model.inmemory.InMemoryModel;

/**
 * Creates a model based on specified type.
 * 
 * @author dgoldenberg
 */
public class ModelFactory {

  public static final String MODEL_IN_MEM = "inmemory";
  public static final String MODEL_ACCUMULO = "accumulo";

  /**
   * Creates a model based on specified type.
   * 
   * @param modelType
   *          the model type
   * @return model instance
   * @throws Exception
   */
  public static Model getModel(String modelType) throws Exception {
    Model model = null;
    if (MODEL_ACCUMULO.equalsIgnoreCase(modelType)) {
      final String[] args = { "-fake", "-u", "root", "-p", StringUtils.EMPTY, "-t", "some-table" };
      model = new AccumuloModel(args);
    } else if (MODEL_IN_MEM.equalsIgnoreCase(modelType)) {
      model = new InMemoryModel();
    } else {
      throw new IllegalArgumentException("Unsupported model: " + modelType);
    }
    return model;
  }
}
