package com.hexastax.kata14.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a table used for storing text models.
 * 
 * @author dgoldenberg
 */
public class Table extends LinkedHashMap<Key, String> {

  private static final long serialVersionUID = 5589784557540064747L;

  private String name = null;

  public Table(String name) {
    this.name = name;
  }

  public List<Map.Entry<Key, String>> getFamily(String family) {
    List<Map.Entry<Key, String>> fam = new ArrayList<Map.Entry<Key, String>>();

    for (Map.Entry<Key, String> entry : entrySet()) {
      String sfam = entry.getKey().getFamily();
      if (sfam.equals(family)) {
        fam.add(entry);
      }
    }

    return fam;
  }

  public void dump() {
    System.out.println("---- Table: " + name + " ----");
    for (Map.Entry<Key, String> entry : this.entrySet()) {
      Key key = entry.getKey();
      String value = entry.getValue();

      System.out.println(">> " + key + "  ------- " + value);
    }
    System.out.println("----------------------------------\n");
  }

}
