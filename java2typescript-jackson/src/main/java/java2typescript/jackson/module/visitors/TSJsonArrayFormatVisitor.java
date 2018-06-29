package java2typescript.jackson.module.visitors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;

import java2typescript.jackson.module.grammar.NumberType;
import java2typescript.jackson.module.grammar.StringType;
import java2typescript.jackson.module.grammar.AnyType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.BooleanType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class TSJsonArrayFormatVisitor extends ABaseTSJsonFormatVisitor<ArrayType> implements JsonArrayFormatVisitor {

  public TSJsonArrayFormatVisitor(ABaseTSJsonFormatVisitor parentHolder) {
    super(parentHolder);
    type = new ArrayType();
  }

  private static AbstractType typeScriptTypeFromJsonType(JsonFormatTypes type) {
    switch (type) {
      case ANY:
        return AnyType.getInstance();
      case BOOLEAN:
        return BooleanType.getInstance();
      case ARRAY:
        return new ArrayType(AnyType.getInstance());
      case INTEGER: //$FALL-THROUGH$
      case NUMBER:
        return NumberType.getInstance();
      case STRING:
        return StringType.getInstance();
      default:
        throw new UnsupportedOperationException();
    }
  }

  @Override
  public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) throws JsonMappingException {
    TSJsonFormatVisitorWrapper visitorWrapper = new TSJsonFormatVisitorWrapper(this);
    handler.acceptJsonFormatVisitor(visitorWrapper, elementType);
    type.setItemType(visitorWrapper.getType());
  }

  @Override
  public void itemsFormat(JsonFormatTypes format) throws JsonMappingException {
    type.setItemType(typeScriptTypeFromJsonType(format));
  }
}
