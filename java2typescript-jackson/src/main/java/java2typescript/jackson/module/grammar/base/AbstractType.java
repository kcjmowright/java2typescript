package java2typescript.jackson.module.grammar.base;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractType {

  public abstract void write(Writer writer) throws IOException;

}
