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
package java2typescript.jackson.module.visitors;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWithSerializerProvider;

import java2typescript.jackson.module.grammar.Module;
import java2typescript.jackson.module.grammar.base.AbstractType;

public abstract class ABaseTSJsonFormatVisitor<T extends AbstractType> implements
    JsonFormatVisitorWithSerializerProvider {

  private final ABaseTSJsonFormatVisitor<?> parentHolder;

  protected T type;

  private SerializerProvider serializerProvider;

  private Module module;

  private Map<JavaType, AbstractType> computedTypes;

  public ABaseTSJsonFormatVisitor(ABaseTSJsonFormatVisitor parentHolder) {
    this.parentHolder = parentHolder;
  }

  public ABaseTSJsonFormatVisitor(Module module) {
    this.parentHolder = null;
    this.module = module;
  }

  public SerializerProvider getProvider() {
    if (parentHolder == null || parentHolder == this) {
      return serializerProvider;
    }
    return parentHolder.getProvider();
  }

  public void setProvider(SerializerProvider provider) {
    if (parentHolder != null && parentHolder != this) {
      parentHolder.setProvider(provider);
    } else {
      serializerProvider = provider;
    }
  }

  public Module getModule(String moduleName) {
    if (parentHolder == null || parentHolder == this) {
      if (moduleName == null || moduleName.equalsIgnoreCase(module.getName())) {
        return module;
      }
      Module newModule = module.getModules().get(moduleName);
      if (newModule == null) {
        newModule = new Module(moduleName);
        module.getModules().put(moduleName, newModule);
      }
      return newModule;
    }
    return parentHolder.getModule(moduleName);
  }

  public Map<JavaType, AbstractType> getComputedTypes() {
    if (parentHolder == null || parentHolder == this) {
      if (computedTypes == null) {
        computedTypes = new HashMap<>();
      }
      return computedTypes;
    }
    return parentHolder.getComputedTypes();
  }

  public T getType() {
    return type;
  }
}
