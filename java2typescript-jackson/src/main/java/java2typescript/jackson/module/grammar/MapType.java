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
import java.util.Objects;

import java2typescript.jackson.module.grammar.base.AbstractType;

public class MapType extends AbstractType {
  private AbstractType valueType;
  private AbstractType keyType;

  public MapType() {
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write("{ [key: string ]: ");
    valueType.write(writer);
    writer.write(";}");
  }

  public AbstractType getValueType() {
    return valueType;
  }

  public void setValueType(AbstractType valueType) {
    this.valueType = valueType;
  }

  public AbstractType getKeyType() {
    return keyType;
  }

  public void setKeyType(AbstractType keyType) {
    this.keyType = keyType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    MapType mapType = (MapType) o;
    return Objects.equals(valueType, mapType.valueType) && Objects.equals(keyType, mapType.keyType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass().hashCode(), valueType, keyType);
  }
}
