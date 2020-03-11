package java2typescript.jackson.module.grammar.base;

import static java2typescript.jackson.module.grammar.base.JavascriptReservedWords.sanitize;

import java.io.IOException;
import java.io.Writer;

abstract public class AbstractPrimitiveType extends AbstractType {

  private String token;

  public AbstractPrimitiveType(String token) {
    this.token = sanitize(token);
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(token);
  }

  public String getToken() {
    return token;
  }
}
