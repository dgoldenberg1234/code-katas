package com.hexastax.kata14.model;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.commons.lang.StringUtils;

import com.hexastax.kata14.model.accumulo.AccumuloModel;
import com.hexastax.kata14.model.inmemory.InMemoryModel;
import com.hexastax.katas.commons.exception.PersistenceException;

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
   * @throws PersistenceException
   * @throws Exception
   */
  public static Model getModel(String modelType) throws PersistenceException {
    Model model = null;
    if (MODEL_ACCUMULO.equalsIgnoreCase(modelType)) {
      final String[] args = { "-fake", "-u", "root", "-p", StringUtils.EMPTY, "-t", "some-table" };
      try {
        model = new AccumuloModel(args);
      } catch (AccumuloException | AccumuloSecurityException ex) {
        throw new PersistenceException("Persistence error.", ex);
      }
    } else if (MODEL_IN_MEM.equalsIgnoreCase(modelType)) {
      model = new InMemoryModel();
    } else {
      throw new IllegalArgumentException("Unsupported model: " + modelType);
    }
    return model;
  }
}
