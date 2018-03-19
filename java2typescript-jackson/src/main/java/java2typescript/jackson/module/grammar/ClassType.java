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
import java.util.TreeMap;

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
    for (Entry<AbstractNamedType, String> entry: resolveImports().entrySet()) {
      writer.write("import { " + entry.getKey().getDefName() + " } from '" + entry.getValue() + "';\n");
    }
    writer.write(format("\nexport interface %s {\n", getDefName()));
    for (Entry<String, AbstractType> entry : fields.entrySet()) {
      writer.write(format("    %s?: ", entry.getKey()));
      entry.getValue().write(writer);
      writer.write(";\n");
    }
    for (String methodName : methods.keySet()) {
      writer.write("    " + methodName);
      this.methods.get(methodName).writeNonLambda(writer);
      writer.write(";\n");
    }
    writer.write("}");
  }

  public Map<String, AbstractType> getFields() {
    return fields;
  }

  public void setFields(Map<String, AbstractType> fields) {
    this.fields = fields;
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
    return "I" + getSimpleName();
  }

  public Map<AbstractNamedType, String> resolveImports() {
    Map<AbstractNamedType, String> imports = new TreeMap<>((AbstractNamedType o1, AbstractNamedType o2) ->
        o1.getDefName().compareToIgnoreCase(o2.getDefName()));
    List<String> packagePathList1 = new ArrayList<>(Arrays.asList(packagePath));

    getFields().values().stream().filter(v -> v instanceof AbstractNamedType).forEach(v -> {
      AbstractNamedType namedType = (AbstractNamedType) v;
      setupImport(imports, packagePathList1, namedType);
    });
    getMethods().values().stream().forEach(m -> {
      m.getParameters().values().stream().filter(v -> v instanceof AbstractNamedType).forEach(v -> {
        AbstractNamedType namedType = (AbstractNamedType) v;
        setupImport(imports, packagePathList1, namedType);
      });
      AbstractType resultType = m.getResultType();
      if(resultType instanceof AbstractNamedType) {
        AbstractNamedType namedType = (AbstractNamedType) resultType;
        setupImport(imports, packagePathList1, namedType);
      }
    });
    return imports;
  }

  private void setupImport(Map<AbstractNamedType, String> imports, List<String> packagePathList1, AbstractNamedType namedType) {
    List<String> packagePathList2 = new ArrayList<>(Arrays.asList(namedType.getPackagePath()));
    imports.put(namedType, resolveImportsPath(packagePathList1, packagePathList2) +
        "/" + namedType.getFileName().replaceAll("\\.ts$", ""));
  }

  /**
   *
   * @param a
   * @param b
   * @return
   */
  protected String resolveImportsPath(List<String> a, List<String> b) {
    if(a.equals(b)) {
      return ".";
    }
    if(a.size() > b.size()) {
      while(a.size() != b.size()) {
        b.add("");
      }
    } else if(a.size() < b.size()) {
      while (a.size() != b.size()) {
        a.add("");
      }
    }
    boolean look = false;
    List<String> stack = new ArrayList<>();
    StringBuilder importPath = new StringBuilder();

    for(int i = a.size(); --i>= 0;) {
      if(look && a.get(i).equalsIgnoreCase(b.get(i))) {
        break;
      } else if(!a.get(i).equalsIgnoreCase(b.get(i))) {
        look = true;
      }
      importPath.append("../");
      if(!"".equalsIgnoreCase(b.get(i))) {
        stack.add(b.get(i));
      }
    }
    Collections.reverse(stack); // <- Flip it and reverse it.
    importPath.append(String.join("/", stack.toArray(new String[]{})));
    return importPath.toString();
  }
}
