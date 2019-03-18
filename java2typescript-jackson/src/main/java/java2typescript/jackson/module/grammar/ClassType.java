package java2typescript.jackson.module.grammar;

import static java.lang.String.format;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java2typescript.jackson.module.PathResolver;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class ClassType extends AbstractNamedType {

  private Map<String, AbstractType> fields = new LinkedHashMap<>();
  private Map<String, List<FunctionType>> methods = new LinkedHashMap<>();
  private Map<String, GenericType> generics = new LinkedHashMap<>();

  /**
   *
   * @param packagePath
   * @param className
   */
  public ClassType(String[] packagePath, String className, Map<String, GenericType> generics) {
    super(packagePath, className);
    if (generics != null && generics.size() > 0) {
      this.generics.putAll(generics);
    }
  }

  @Override
  public void writeDef(Writer writer) throws IOException {
    Map<AbstractNamedType, String> imports = resolveImports();
    if (imports.size() > 0) {
      imports.entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(AbstractNamedType::getDefName))).forEach(entry -> {
        try {
          writer.write("import { " + entry.getKey().getDefName() + " } from '" + entry.getValue() + "';\n");
        } catch(IOException e) {
          throw new RuntimeException(e);
        }
      });
      writer.write("\n");
    }
    if (this.innerTypes.size() > 0) {
      for( AbstractNamedType innerType: getInnerTypes().stream().sorted(Comparator.comparing(AbstractNamedType::getDefName)).collect(Collectors.toList())) {
        innerType.writeDef(writer);
        writer.write("\n");
      }
      writer.write("\n");
    }
    writer.write(format("export interface %s%s {\n", getDefName(), getGenericTypesSignature()));
    fields.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
      try {
        writer.write(format("  %s?: ", entry.getKey()));
        entry.getValue().write(writer);
        writer.write(";\n");
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    });
    methods.keySet().stream().sorted().forEach(methodName -> {
      try {
        for (FunctionType functionType: this.methods.get(methodName).stream().sorted(new FunctionTypeComparator()).collect(Collectors.toList())) {
          writer.write("  " + methodName);
          functionType.writeNonLambda(writer);
          writer.write(";\n");
        }
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    });
    writer.write("}\n");
  }

  public Map<String, AbstractType> getFields() {
    return fields;
  }

  public Map<String, List<FunctionType>> getMethods() {
    return methods;
  }

  public void addMethod(String methodName, FunctionType functionType) {
    if (functionType == null) {
      return;
    }
    methods.computeIfAbsent(methodName, (k) -> new ArrayList<>()).add(functionType);
  }

  public String getGenericTypesSignature() {
    StringBuilder signature = new StringBuilder();

    generics.values().stream().forEach(genericType -> {
      if (signature.length() > 0) {
        signature.append(", ");
      }
      signature.append(genericType.getName());
    });
    return (signature.length() > 0) ? "<" + signature.toString() + ">" : "";
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(getDefName());
    if (generics.size() > 0) {
      writer.write("<");
      for (int i = 0; i < generics.values().size(); i++) {
        if (i > 0) {
          writer.write(", ");
        }
        AnyType.getInstance().write(writer);
      }
      writer.write(">");
    }
  }

  @Override
  public String getDefName() {
    return "I" + getPrefix() + getSimpleName();
  }

  public Map<AbstractNamedType, String> resolveImports() {
    Map<AbstractNamedType, String> imports = new TreeMap<>((AbstractNamedType o1, AbstractNamedType o2) ->
        o1.getDefName().compareToIgnoreCase(o2.getDefName()));
    List<String> packagePathList1 = new ArrayList<>(Arrays.asList(packagePath));
    PathResolver pathResolver = PathResolver.getResolver();

    getFields().values().stream().filter(f -> {
      boolean include = true;
      AbstractType field = f;

      while ((field instanceof MapType) || (field instanceof ArrayType)){
        field = (field instanceof MapType) ? ((MapType)field).getValueType() : ((ArrayType)field).getItemType();
      }
      if (field instanceof AbstractNamedType) {
        AbstractNamedType enclosingType = ((AbstractNamedType)field).getEnclosingType();
        include = enclosingType == null || !this.getName().equalsIgnoreCase(enclosingType.getName());
      }
      return include;
    }).forEach(v -> pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, v)));

    methods.values().stream().forEach(functionTypes -> {
      for (FunctionType m: functionTypes) {
        m.getParameters().values().stream()
            .forEach(v -> pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, v)));
        pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, m.getResultType()));
      }
    });
    return imports;
  }


}
