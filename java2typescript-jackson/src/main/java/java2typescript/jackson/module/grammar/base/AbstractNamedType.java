package java2typescript.jackson.module.grammar.base;

import java.io.IOException;
import java.io.Writer;

/** Type referenced by its name and capable of writing its own definition */
abstract public class AbstractNamedType extends AbstractType {

  protected final String name;

  public AbstractNamedType(String className) {
    this.name = className;
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(name);
  }

  public String getName() {
    return name;
  }

  abstract public void writeDef(Writer writer) throws IOException;
}
