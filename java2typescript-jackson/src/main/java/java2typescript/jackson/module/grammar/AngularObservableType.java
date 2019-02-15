package java2typescript.jackson.module.grammar;

import java.io.IOException;
import java.io.Writer;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

/**
 *
 */
public class AngularObservableType extends AbstractNamedType {

  private AbstractType type;

  public AngularObservableType(AbstractType type) {
    super(new String[]{ "rxjs" }, "Observable");
    this.type = type;
  }

  public AbstractType getType() {
    return type;
  }

  public void setType(AbstractType type) {
    this.type = type;
  }

  @Override
  public void write(Writer writer) throws IOException {
    if (type == null) {
      writer.write("Observable");
    } else {
      writer.write("Observable<");
      type.write(writer);
      writer.write(">");
    }
  }

}
