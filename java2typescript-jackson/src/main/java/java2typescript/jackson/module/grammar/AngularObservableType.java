package java2typescript.jackson.module.grammar;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AngularObservableType that = (AngularObservableType) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return 31 * getClass().hashCode() + Objects.hash(type);
  }
}
