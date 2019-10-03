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
package java2typescript.jackson.module;

import static com.google.common.collect.Lists.newArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;

import java2typescript.jackson.module.grammar.Module;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;

public class DefinitionGeneratorTest {

  @JsonTypeName("ChangedEnumName")
  static enum Enum {
    VAL1, VAL2, VAL3
  }

  class GenericClass<T> {
    public T someField;
  }

  class TestClass {
    public String _String;
    public boolean _boolean;
    public Boolean _Boolean;
    public int _int;
    public float _float;
    public String[] stringArray;
    public Map<Integer, Boolean> map;
    public TestClass recursive;
    public TestClass[] recursiveArray;
    public ArrayList<String> stringArrayList;
    public Collection<Boolean> booleanCollection;
    public Enum _enum;
    public Optional<Integer> _optionalInteger;
    public String test;

    public String aMethod(boolean recParam, String param2) {
      return "toto";
    }

    public String test() {
      return this.test;
    }
  }

  public class StringClass extends GenericClass<String> {
  }

  @Test
  public void testTypeScriptDefinition() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    DefinitionGenerator generator = new DefinitionGenerator(mapper);
    String output;
    try (StringWriter out = new StringWriter();
         InputStream in = getClass().getClassLoader().getResourceAsStream("definition-generator-test.ts")) {
      Module module = generator.generateTypeScript(newArrayList(TestClass.class, StringClass.class));
      writeModule(out, module);
      output = out.getBuffer().toString();
      assertNotNull(in);
      String expected = inputStreamToString(in);
      assertEquals(expected, output);
    }
  }

  private void writeModule(Writer out, Module module) {
    try {
      module.writeDef(out);
      for (AbstractNamedType type : module.getNamedTypes().values().stream().sorted(Comparator.comparing(AbstractNamedType::getDefName)).collect(Collectors.toList())) {
        out.write("\n");
        out.write(type.getFullyQualifiedName());
        out.write("\n");
        type.writeDef(out);
      }
      module.getModules().values().stream().forEach(child -> writeModule(out, child));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String inputStreamToString(InputStream in) throws IOException {
    try (StringWriter out = new StringWriter();
         InputStreamReader reader = new InputStreamReader(in)) {
      char[] buffer = new char[1028];
      int read;
      while ((read = reader.read(buffer, 0, buffer.length)) != -1) {
        out.write(buffer, 0, read);
      }
      return out.toString();
    }
  }
}

