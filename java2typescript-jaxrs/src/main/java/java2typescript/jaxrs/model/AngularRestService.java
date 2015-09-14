/*******************************************************************************
 * Copyright 2015 Raphael Jolivet
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

import com.google.common.base.CaseFormat;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.VoidType;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class AngularRestService extends BaseModel {

  private String path;
  private String angularModule;
  private String baseModule;
  private String subModule;
  private ClassType classDef;

  public AngularRestService(){
    super();
  }

  public AngularRestService(String name, String path, String angularModule, String baseModule, String subModule) {
    super(name);
    this.path = path;
    this.angularModule = angularModule;
    this.baseModule = baseModule;
    this.subModule = subModule;
  }

  private final Map<String, RestMethod> methods = new HashMap<String, RestMethod>();

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getAngularModule() {
    return angularModule;
  }

  public void setAngularModule(String angularModule) {
    this.angularModule = angularModule;
  }

  public String getBaseModule() {
    return baseModule;
  }

  public void setBaseModule(String baseModule) {
    this.baseModule = baseModule;
  }

  public String getSubModule() {
    return subModule;
  }

  public void setSubModule(String subModule) {
    this.subModule = subModule;
  }

  public ClassType getClassDef() {
    return classDef;
  }

  public void setClassDef(ClassType classDef) {
    this.classDef = classDef;
  }

  public Map<String, RestMethod> getMethods() {
    return methods;
  }


  public void write(Writer writer) throws IOException {

    writer.write(format("class _%s implements %s {\n", getName(), getName()));

    writer.write("  private http: ng.IHttpService;\n");
    writer.write(format("  private baseUrl: string = contextUrl + '%s';\n\n", getPath()).replace("{","<%= ").replace("}"," %>"));

    writer.write("  constructor (httpService: ng.IHttpService) {\n");
    writer.write("    this.http = httpService;\n");
    writer.write("  }\n\n");

    for (Map.Entry<String, RestMethod> entry : getMethods().entrySet()) {
      String methodName = entry.getKey();
      RestMethod restMethod = entry.getValue();
      FunctionType functionType = getClassDef().getMethods().get(methodName);
      boolean hasBeanParams = false;

      writer.write("  " + methodName);
      functionType.writeNonLambda(writer);
      writer.write(" {\n");

      // URL Template
      writer.write(format("    var urlTmpl = _.template(this.baseUrl + '%s');\n",
          restMethod.getPath().replace("{","<%= ").replace("}"," %>")));

      // Path Params
      writer.write("    var pathParams = {\n");
      int pathParamCount = 0;
      for ( Param param: restMethod.getParams()) {
        if (param.getType() == ParamType.PATH) {
          if (pathParamCount++ > 0) {
            writer.write(",\n");
          }
          writer.write(format("      %s: %s", param.getName(), param.getName()));
        } else if (param.getType() == ParamType.BEAN) {
          hasBeanParams = true;
        }
      }
      writer.write("\n    };\n");

      // Query Params
      writer.write("    var params = {\n");

      int queryParamCount = 0;
      for (Param param : restMethod.getParams()) {
        if (param.getType() == ParamType.QUERY) {
          if (queryParamCount++ > 0) {
            writer.write(",\n");
          }
          writer.write(format("      %s: %s", param.getName(), param.getName()));
        }
      }
      writer.write("\n    };\n");

      // Bean Params
      if (hasBeanParams) {
        writer.write("    var key;\n\n");
        for (Param param : restMethod.getParams()) {
          if (param.getType() == ParamType.BEAN) {
            writer.write(format("    for ( key in %s ) {\n", param.getName()));
            writer.write(format("      params[key] = %s[key];\n", param.getName()));
            writer.write("    }\n");
          }
        }
      }

      writer.write("\n");

      if (restMethod.getHttpMethod() == HttpMethod.GET) {
        writer.write("    return this.http({\n");
        writer.write("      url: urlTmpl(pathParams),\n");
        writer.write("      method: 'GET',\n");
        writer.write("      responseType: 'json',\n");
        writer.write("      params: params\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.POST) {
        writer.write("    return this.http({\n");
        writer.write("      url: urlTmpl(pathParams),\n");
        writer.write("      method: 'POST',\n");
        for (Param param: restMethod.getParams()) {
          if (param.getType() == ParamType.BODY){
            writer.write(format("      data: %s\n", param.getName() ));
            break;
          }
        }
        if (!(functionType.getResultType() instanceof VoidType)) {
          writer.write("      responseType: 'json',\n");
        }
        writer.write("      params: params\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.PUT) {
        writer.write("    return this.http({\n");
        writer.write("      url: urlTmpl(pathParams),\n");
        writer.write("      method: 'PUT',\n");
        for (Param param: restMethod.getParams()) {
          if (param.getType() == ParamType.BODY){
            writer.write(format("      data: %s\n", param.getName() ));
            break;
          }
        }
        if (!(functionType.getResultType() instanceof VoidType)) {
          writer.write("      responseType: 'json',\n");
        }
        writer.write("      params: params\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.DELETE) {
        writer.write("    return this.http({\n");
        writer.write("      url: urlTmpl(pathParams),\n");
        writer.write("      method: 'DELETE',\n");
        writer.write("      responseType: 'json',\n");
        writer.write("      params: params\n");
        writer.write("    });\n");
      }

      writer.write("  }\n\n");
    }

    writer.write("}\n\n");

    String angularResourceName = CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL,
        String.format("%s-%s-%s",
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, baseModule),
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, subModule),
            CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_HYPHEN, lowerCamelName)));

    writer.write(format("%s.factory('%s', [\n", angularModule, angularResourceName));
    writer.write("    '$http',\n");
    writer.write(format("    function($http: ng.IHttpService) : %s {\n", getName()));
    writer.write(format("      return new _%s($http);\n", getName()));
    writer.write("    }\n");
    writer.write("]);\n");
  }

}
