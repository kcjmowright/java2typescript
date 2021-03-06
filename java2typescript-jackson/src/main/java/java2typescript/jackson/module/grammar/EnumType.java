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

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;

public class EnumType extends AbstractNamedType {

  private Set<String> values = new HashSet<>();

  public EnumType(String[] packagePath, String className) {
    super(packagePath, className);
  }

  @Override
  public void writeDef(Writer writer) throws IOException {
    writer.write(format("export type %s = ", getSimpleName()));

    Iterator<String> i = values.iterator();
    while (i.hasNext()) {
      writer.write(format("'%s'", i.next()));
      if (i.hasNext()) {
        writer.write(" | ");
      }
    }
    writer.write(";\n/* tslint:disable:object-literal-sort-keys */\n\n");
    writer.write(format("export const %s = {\n", getSimpleName()));
    for (String value : values) {
      writer.write(format("  '%s': '%s' as %s,\n", value, value, getSimpleName()));
    }
    writer.write("  values: function() {\n    return [\n");

    i = values.iterator();
    while (i.hasNext()) {
      writer.write(format("      this['%s']", i.next()));
      if (i.hasNext()) {
        writer.write(",\n");
      }
    }
    writer.write("\n    ];\n  }\n};\n");
    writer.write("/* tslint:enable:object-literal-sort-keys */\n");
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(getDefName());
  }

  public Set<String> getValues() {
    return values;
  }

  public void setValues(Set<String> values) {
    this.values = values;
  }
}
