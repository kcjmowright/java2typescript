package java2typescript.jackson.module.grammar;

import java.io.IOException;
import java.io.Writer;

import java2typescript.jackson.module.grammar.base.AbstractType;

public class AngularHttpClient extends AbstractType {


  public void write(Writer writer) throws IOException {
    writer.write("HttpClient");
  }

}
