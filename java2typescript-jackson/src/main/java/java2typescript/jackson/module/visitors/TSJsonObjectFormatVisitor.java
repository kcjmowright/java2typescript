package java2typescript.jackson.module.visitors;

import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import static com.fasterxml.jackson.databind.PropertyName.NO_NAME;

import static java2typescript.jackson.module.visitors.TSJsonFormatVisitorWrapper.getTSTypeForHandler;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.Transient;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java2typescript.jackson.module.grammar.AnyType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.GenericType;
import java2typescript.jackson.module.grammar.VoidType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class TSJsonObjectFormatVisitor extends ABaseTSJsonFormatVisitor<ClassType> implements JsonObjectFormatVisitor {

  private Class clazz;
  private Map<String, GenericType> generics = new LinkedHashMap<>();

  public TSJsonObjectFormatVisitor(ABaseTSJsonFormatVisitor<?> parentHolder, String className, Class clazz) {
    super(parentHolder);
    String[] packagePath = clazz.getPackage().getName().split("\\.");

    Arrays.stream(clazz.getTypeParameters())
      .forEach(typeVariable -> {
        GenericType genericType = new GenericType(typeVariable.getName());
        generics.put(genericType.getName(), genericType);
      });
    type = new ClassType(packagePath, className, generics);
    this.clazz = clazz;
  }

  private void addField(String name, AbstractType fieldType) {
    type.getFields().put(name, fieldType);
  }

  private boolean isIgnoreableMethod(Method method, BeanInfo beanInfo) {
    boolean ignorable = false;

    if ("toString".equalsIgnoreCase(method.getName()) ||
        "equals".equalsIgnoreCase(method.getName()) ||
        "hashCode".equalsIgnoreCase(method.getName())) {
      ignorable = true;
    } else {
      boolean annotated = Arrays.stream(method.getAnnotations()).anyMatch(annotation -> (
          "@javax.ws.rs.GET()".equalsIgnoreCase(annotation.toString()) ||
          "@javax.ws.rs.POST()".equalsIgnoreCase(annotation.toString()) ||
          "@javax.ws.rs.PUT()".equalsIgnoreCase(annotation.toString()) ||
          "@javax.ws.rs.DELETE()".equalsIgnoreCase(annotation.toString()))
      );

      if (!annotated) {
        ignorable = Arrays.stream(beanInfo.getPropertyDescriptors()).anyMatch(property ->
            method.equals(property.getReadMethod()) ||
                method.equals(property.getWriteMethod()));
      }
    }
    return ignorable;
  }

  public void addPublicMethods() {
    final AnnotatedClass annotatedClass = AnnotatedClass.construct(this.getClass(), new JacksonAnnotationIntrospector(), null);

    Arrays.stream(this.clazz.getDeclaredMethods()).filter(method -> {
      boolean add = true;

      if (!isPublic(method.getModifiers()) || isStatic(method.getModifiers())) {
        add = false;
      } else if (method.getAnnotation(Transient.class) != null) { // Ignore @Transient methods
        add = false;
      } else {
        // Exclude ignorable methods.
        try {
          BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
          if (isIgnoreableMethod(method, beanInfo)) {
            add = false;
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
      return add;
    }).forEach(method -> addMethod(annotatedClass, method));
  }

  private AbstractType getTSTypeForClass(AnnotatedMember member) {

    TypeBindings bindings = new TypeBindings(TypeFactory.defaultInstance(), member.getDeclaringClass());
    BeanProperty prop = new BeanProperty.Std(new PropertyName(member.getName()), member.getType(bindings), NO_NAME, new AnnotationMap(),
        member, PropertyMetadata.STD_OPTIONAL);

    try {
      return getTSTypeForProperty(prop);
    } catch (JsonMappingException e) {
      throw new RuntimeException(e);
    }
  }

  private void addMethod(AnnotatedClass annotatedClass, Method method) {
    FunctionType function = new FunctionType();

    AnnotatedMethod annotMethod = new AnnotatedMethod(annotatedClass, method, new AnnotationMap(), new AnnotationMap[] {});
    AbstractType resultType = getTSTypeForClass(annotMethod);
    function.setResultType(resultType);

    for (int i = 0; i < annotMethod.getParameterCount(); i++) {
      AnnotatedParameter param = annotMethod.getParameter(i);
      String name = "param" + i;
      function.getParameters().put(name, getTSTypeForClass(param));
    }
    this.type.getMethods().put(method.getName(), function);
  }

  @Override
  public void property(BeanProperty writer) throws JsonMappingException {
    addField(writer.getName(), getTSTypeForProperty(writer));
  }

  @Override
  public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint)
      throws JsonMappingException {
    addField(name, getTSTypeForHandler(this, handler, propertyTypeHint));
  }

  public void property(String name) throws JsonMappingException {
    addField(name, AnyType.getInstance());
  }

  @Override
  public void optionalProperty(BeanProperty writer) throws JsonMappingException {
    AbstractType abstractType = getTSTypeForProperty(writer);

    if (generics.size() > 0) {
      Map<String, GenericType> genericTypeMap = parseGenericPropertyTypeName(((BeanPropertyWriter) writer).getGenericPropertyType().getTypeName());

      try {
        abstractType = (AbstractType) abstractType.clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }

      if (abstractType instanceof ArrayType && genericTypeMap.size() > 0) {
        ((ArrayType) abstractType).setItemType(genericTypeMap.values().iterator().next());
      }
    }
    addField(writer.getName(), abstractType);
  }

  @Override
  public void optionalProperty(String name, JsonFormatVisitable handler, JavaType propertyTypeHint)
      throws JsonMappingException {
    addField(name, getTSTypeForHandler(this, handler, propertyTypeHint));
  }

  protected AbstractType getTSTypeForProperty(BeanProperty writer) throws JsonMappingException {
    if (writer == null) {
      throw new IllegalArgumentException("Null writer");
    }
    JavaType type = writer.getType();
    AbstractType abstractType = AnyType.getInstance();

    if (type.getRawClass().equals(Void.TYPE)) {
      abstractType = VoidType.getInstance();
    } else {
      try {
        JsonSerializer<Object> ser = getSer(writer);
        if (ser != null) {
          abstractType = getTSTypeForHandler(this, ser, type);
        }
      } catch (Exception ignore) {}
    }
    return abstractType;
  }

  protected JsonSerializer<Object> getSer(BeanProperty writer) throws JsonMappingException {
    JsonSerializer<Object> ser = null;
    if (writer instanceof BeanPropertyWriter) {
      ser = ((BeanPropertyWriter) writer).getSerializer();
    }
    if (ser == null) {
      ser = getProvider().findValueSerializer(writer.getType(), writer);
    }
    return ser;
  }

  private Map<String, GenericType> parseGenericPropertyTypeName(String name) {
    Map<String, GenericType> genericTypes = new LinkedHashMap<>();

    try {
      Pattern pattern = Pattern.compile("<([^>]*)>");
      Matcher matcher = pattern.matcher(name);

      for (int i = 1; matcher.find(); i++) {
        GenericType genericType = generics.get(matcher.group(i));

        if (genericType != null) {
          genericTypes.put(genericType.getName(), genericType);
        }
      }
    } catch (Exception ignore) {}
    return genericTypes;
  }
}
