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

package java2typescript.jaxrs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java2typescript.jackson.module.grammar.FunctionType;

import java.util.List;
import java.util.Map;

public class RestMethod extends FunctionType {

  private String name;
  private String path;
  private List<Param> params;
  private Map<String, Param> pathParams;
  private HttpMethod httpMethod;
  private String producesContentType = "application/json";

  public String getPath() {
    return path;
  }

  @JsonIgnore
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<Param> getParams() {
    return params;
  }

  public void setParams(List<Param> params) {
    this.params = params;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }

  public void setProducesContentType(String contentType) {
    this.producesContentType = contentType;
  }

  public String getProducesContentType() {
    return this.producesContentType;
  }
}
