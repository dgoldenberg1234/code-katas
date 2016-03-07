package com.hexastax.kata14.model;

/**
 * Represents a key for reads and writes into models.
 * 
 * @author dgoldenberg
 */
public class Key {

  private String family = null;
  private String qualifier = null;

  public Key(String family, String qualifier) {
    this.family = family;
    this.qualifier = qualifier;
  }

  public String getFamily() {
    return family;
  }

  public String getQualifier() {
    return qualifier;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((family == null)
      ? 0 : family.hashCode());
    result = prime * result + ((qualifier == null)
      ? 0 : qualifier.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Key other = (Key) obj;
    if (family == null) {
      if (other.family != null)
        return false;
    } else if (!family.equals(other.family))
      return false;
    if (qualifier == null) {
      if (other.qualifier != null)
        return false;
    } else if (!qualifier.equals(other.qualifier))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Key [family=" + family + ", qualifier=" + qualifier + "]";
  }

}
