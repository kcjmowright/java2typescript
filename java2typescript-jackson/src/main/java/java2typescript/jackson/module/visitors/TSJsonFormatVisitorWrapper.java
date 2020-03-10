/*******************************************************************************
 * Copyright 2013 Raphael Jolivet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package java2typescript.jackson.module.visitors;

import static java2typescript.jackson.module.grammar.base.JavascriptReservedWords.sanitizeAll;
import static java2typescript.jackson.module.grammar.base.JavascriptReservedWords.sanitizeJavaName;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonBooleanFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNullFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.type.SimpleType;

import java2typescript.jackson.module.grammar.EnumType;
import java2typescript.jackson.module.grammar.Module;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class TSJsonFormatVisitorWrapper extends ABaseTSJsonFormatVisitor implements JsonFormatVisitorWrapper {

  public TSJsonFormatVisitorWrapper(ABaseTSJsonFormatVisitor parentHolder) {
    super(parentHolder);
  }

  public TSJsonFormatVisitorWrapper(Module module) {
    super(module);
  }

  /**
   * Visit types recursively, or return a cached response.
   * @param baseVisitor
   * @param handler
   * @param typeHint
   * @return
   * @throws JsonMappingException
   */
  public static AbstractType getTSTypeForHandler(ABaseTSJsonFormatVisitor<?> baseVisitor,
      JsonFormatVisitable handler, JavaType typeHint) throws JsonMappingException {
    AbstractType computedType = baseVisitor.getComputedTypes().get(typeHint);

    if (computedType == null) { // computed type is not cached.
      TSJsonFormatVisitorWrapper visitor = new TSJsonFormatVisitorWrapper(baseVisitor);
      handler.acceptJsonFormatVisitor(visitor, typeHint);
      baseVisitor.getComputedTypes().put(typeHint, visitor.getType());
      computedType = visitor.getType();
    }
    return computedType;
  }

  private <T extends ABaseTSJsonFormatVisitor<?>> T setTypeAndReturn(T actualVisitor) {
    type = actualVisitor.getType();
    return actualVisitor;
  }

  /**
   * Either Java simple name or @JsonTypeName annotation.
   * @param type
   * @return
   */
  private String[] getNames(JavaType type) {
    String[] retValue;
    JsonTypeName typeName = type.getRawClass().getAnnotation(JsonTypeName.class);
    // If @JsonTypeName annotation
    if (typeName != null) {
      retValue = new String[] { null, sanitizeJavaName(typeName.value()) };
    } else { // Java simple name
      Class clazz = type.getRawClass();
      retValue = new String[] { sanitizeJavaName(clazz.getPackage().getName()), sanitizeJavaName(clazz.getCanonicalName())};
    }
    return retValue;
  }

  private TSJsonObjectFormatVisitor useNamedClassOrParse(JavaType javaType) throws JsonMappingException {
    TSJsonObjectFormatVisitor visitor = null;
    String[] names = getNames(javaType);
    String moduleName = names[0];
    String className = names[1];
    AbstractNamedType namedType = getModule(moduleName).getNamedTypes().get(className);

    if (namedType == null) {
      Class clazz = javaType.getRawClass();
      visitor = new TSJsonObjectFormatVisitor(this, className, clazz);
      type = visitor.getType();
      getModule(moduleName).getNamedTypes().put(visitor.getType().getName(), visitor.getType());
      visitor.addPublicMethods();
    } else {
      type = namedType;
    }
    return visitor;
  }

  private EnumType parseEnumOrGetFromCache(JavaType javaType) throws JsonMappingException {
    String[] names = getNames(javaType);
    String moduleName = names[0];
    String className = names[1];
    Class enclosingClass = javaType.getRawClass().getEnclosingClass();
    String[] packagePath = sanitizeAll(moduleName.split("\\."));
    EnumType enumType = (EnumType)getModule(moduleName).getNamedTypes().get(className);

    if (enumType == null) {
      enumType = new EnumType(packagePath, className);
      for (Object val : javaType.getRawClass().getEnumConstants()) {
        enumType.getValues().add(val.toString());
      }
      if (enclosingClass == null) {
        getModule(moduleName).getNamedTypes().put(className, enumType);
      } else {
        AbstractNamedType enclosingClassType = getEnclosingClassType(moduleName, enclosingClass);
        enumType.setEnclosingType(enclosingClassType);
        enclosingClassType.getInnerTypes().add(enumType);
      }
    }
    return enumType;
  }

  private AbstractNamedType getEnclosingClassType(String moduleName, Class enclosingClass) throws JsonMappingException {
    AbstractNamedType enclosingClassType = getModule(moduleName).getNamedTypes().get(sanitizeJavaName(enclosingClass.getCanonicalName()));

    if (enclosingClassType == null) {
      JavaType javaHint = SimpleType.constructUnsafe(enclosingClass);

      enclosingClassType =
          (AbstractNamedType) getTSTypeForHandler(this, getProvider().findValueSerializer(enclosingClass), javaHint);
    }
    return enclosingClassType;
  }

  @Override
  public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
    return useNamedClassOrParse(type);
  }

  @Override
  public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonArrayFormatVisitor(this));
  }

  @Override
  public JsonStringFormatVisitor expectStringFormat(JavaType jType) throws JsonMappingException {
    if (jType.getRawClass().isEnum()) {
      type = parseEnumOrGetFromCache(jType);
      return null;
    }
    return setTypeAndReturn(new TSJsonStringFormatVisitor(this));
  }

  @Override
  public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonNumberFormatVisitor(this));
  }

  @Override
  public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonNumberFormatVisitor(this));
  }

  @Override
  public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonBooleanFormatVisitor(this));
  }

  @Override
  public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonNullFormatVisitor(this));
  }

  @Override
  public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonAnyFormatVisitor(this));
  }

  @Override
  public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
    return setTypeAndReturn(new TSJsonMapFormatVisitor(this));
  }

}
