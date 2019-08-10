package net.limbomedia.dns.model;

import java.io.Serializable;

public class UpdateResult implements Serializable {

  private static final long serialVersionUID = 1L;

  private boolean changed;
  private String value;

  public UpdateResult() {

  }

  public UpdateResult(boolean changed, String value) {
    this.changed = changed;
    this.value = value;
  }

  public boolean isChanged() {
    return changed;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "UpdateResult [changed=" + changed + ", value=" + value + "]";
  }

}
