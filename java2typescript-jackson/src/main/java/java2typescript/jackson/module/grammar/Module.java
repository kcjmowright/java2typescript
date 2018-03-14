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

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class Module {

  private String name;

  private Map<String, Module> modules = new HashMap<>();

  private Map<String, AbstractNamedType> namedTypes = new HashMap<>();

  private Map<String, AbstractType> vars = new LinkedHashMap<>();

  public Module(String name) {
    this.name = name;
  }

  public Map<String, AbstractNamedType> getNamedTypes() {
    return namedTypes;
  }

  public Map<String, AbstractType> getVars() {
    return vars;
  }

  public Map<String, Module> getModules() {
    return modules;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void write(Writer writer) throws IOException {

    for (Module module : modules.values()) {
      module.write(writer);
      writer.write("\n\n");
    }

    for (AbstractNamedType type : namedTypes.values()) {
      writer.write("export ");
      type.writeDef(writer);
      writer.write("\n\n");
    }

    for (AbstractType type : getVars().values()) {
      type.write(writer);
      writer.write("\n\n");
    }

    writer.flush();
  }

}
