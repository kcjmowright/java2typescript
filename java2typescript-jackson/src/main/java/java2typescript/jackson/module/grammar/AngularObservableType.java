/*******************************************************************************
 * Copyright 2015 Justin Wright
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

import java2typescript.jackson.module.grammar.base.AbstractType;

/**
 *
 */
public class AngularObservableType extends AbstractType {

  private AbstractType type;

  public AngularObservableType(AbstractType type) {
    this.type = type;
  }

  public AbstractType getType() {
    return type;
  }

  public void setType(AbstractType type) {
    this.type = type;
  }

  public void write(Writer writer) throws IOException {
    if (type == null) {
      writer.write("Observable");
    } else {
      writer.write("Observable<");
      type.write(writer);
      writer.write(">");
    }
  }

}
