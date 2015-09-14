package java2typescript.jaxrs;

import java.util.Date;

public class AggregationResultDTO {

  String value;

  Date timestamp;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
