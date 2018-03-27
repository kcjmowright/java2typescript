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
import java.util.ArrayList;
import java.util.List;

import java2typescript.jackson.module.Dasherize;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;

public class EnumType extends AbstractNamedType {

  private List<String> values = new ArrayList<>();

  public EnumType(String[] packagePath, String className) {
    super(packagePath, className);
  }

  @Override
  public void writeDef(Writer writer) throws IOException {
    writer.write(format("export interface %s {\n", getDefName()));
    for (String value : values) {
      writer.write(format("    %s: string,\n", value));
    }
    writer.write("}\n\n");

    writer.write(format("export const %s: %s = {\n", getSimpleName(), getDefName()));
    for (String value : values) {
      writer.write(format("    %s: '%s',\n", value, value));
    }
    writer.write("};");
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(getDefName());
  }

  @Override
  public String getDefName() {
    return "I" + getSimpleName();
  }

  @Override
  public String getFileName() {
    return Dasherize.convert(getSimpleName()) + ".ts";
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }

}
