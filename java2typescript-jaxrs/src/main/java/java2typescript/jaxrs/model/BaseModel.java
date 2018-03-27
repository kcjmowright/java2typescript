package java2typescript.jaxrs.model;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;

public abstract class BaseModel extends AbstractNamedType {

  public BaseModel(String[] packagePath, String name) {
    super(packagePath, name);
  }

}
