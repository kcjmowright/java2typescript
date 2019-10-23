package java2typescript.jackson.module.grammar.base;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class AbstractType implements Cloneable {

  public AbstractType() {
    super();
  }

  public abstract void write(Writer writer) throws IOException;

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String toJS() throws IOException {
    try ( StringWriter writer = new StringWriter() ) {
      this.write(writer);
      return writer.toString();
    }
  }

}
