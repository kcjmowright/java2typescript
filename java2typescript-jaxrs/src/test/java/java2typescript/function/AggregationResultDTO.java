package java2typescript.function;

import java.util.Date;

public class AggregationResultDTO {

  String value;

  Date timestamp;

  Integer in;

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

  public Integer getIn() {
    return in;
  }

  public void setIn(Integer in) {
    this.in = in;
  }
}
