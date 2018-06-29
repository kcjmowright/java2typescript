package java2typescript.jackson.module.grammar;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;

import java.io.IOException;
import java.io.Writer;

public class GenericType extends AbstractNamedType {

  public GenericType() {
    this("T");
  }

  public GenericType(String name) {
    super(new String[0], name);
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(name);
  }
}
