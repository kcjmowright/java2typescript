package java2typescript.jaxrs.model;

import java.io.IOException;
import java.io.Writer;

import static java.lang.String.format;

public class AngularModule extends BaseModel {

  public AngularModule(String name) {
    super(new String[]{""}, name);
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(format("// @todo", name, lowerCamelName));
  }

}
