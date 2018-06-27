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
package java2typescript.jackson.module.grammar;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.TreeMap;

import java2typescript.jackson.module.PathResolver;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class ClassType extends AbstractNamedType {

  static private ClassType OBJECT_TYPE = new ClassType(new String[]{ "" }, "Object");

  private Map<String, AbstractType> fields = new LinkedHashMap<>();
  private Map<String, FunctionType> methods = new LinkedHashMap<>();

  /**
   *
   * @param packagePath
   * @param className
   */
  public ClassType(String[] packagePath, String className) {
    super(packagePath, className);
  }

  /**
   *
   * @return ClassType
   */
  public static ClassType getObjectClass() {
    return OBJECT_TYPE;
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
    writer.write(format("export interface %s {\n", getDefName()));
    fields.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
      try {
        writer.write(format("  %s?: ", entry.getKey()));
        entry.getValue().write(writer);
        writer.write(";\n");
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    });
    getMethods().keySet().stream().sorted().forEach(methodName -> {
      try {
        writer.write("  " + methodName);
        this.methods.get(methodName).writeNonLambda(writer);
        writer.write(";\n");
      } catch(IOException e) {
        throw new RuntimeException(e);
      }
    });
    writer.write("}\n");
  }

  public Map<String, AbstractType> getFields() {
    return fields;
  }

  public Map<String, FunctionType> getMethods() {
    return methods;
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(getDefName());
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

    getFields().values().stream().forEach(v -> {
      pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, v));
    });
    getMethods().values().stream().forEach(m -> {
      m.getParameters().values().stream().forEach(v -> {
        pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, v));
      });
      pathResolver.setupImport(imports, packagePathList1, pathResolver.resolveNamedType(imports, m.getResultType()));
    });
    return imports;
  }

}
