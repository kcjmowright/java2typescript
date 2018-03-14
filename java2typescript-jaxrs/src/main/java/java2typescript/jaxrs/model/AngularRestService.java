package java2typescript.jaxrs.model;

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
  private ClassType classDef;

  public AngularRestService(){
    super();
  }

  public AngularRestService(String name, String path, String angularModule, String baseModule) {
    super(name);
    this.path = path;
    this.angularModule = angularModule;
    this.baseModule = baseModule;
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

  public ClassType getClassDef() {
    return classDef;
  }

  public void setClassDef(ClassType classDef) {
    this.classDef = classDef;
  }

  public Map<String, RestMethod> getMethods() {
    return methods;
  }

  public String getContextUrlPath() {
    return "@" + getAngularModule() + "/context-url";
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write("import { Injectable } from '@angular/core';\n");
    writer.write("import { HttpClient } from '@angular/common/http';\n");
    writer.write("import { Observable } from 'rxjs/Observable'\n\n");

    // @todo Import DTOs

    // @todo Import context URL?
    writer.write("import {contextUrl} from '" + getContextUrlPath() + "'; // @todo Fix me!\n\n");

    writer.write("@Injectable()\n");
    writer.write(format("export class %s implements %s {\n", getName(), "I" + getName()));

    String baseUrlPath = getPath().replace("{","${encodeURIComponent(").replace("}",")}").trim();

    if("/".equalsIgnoreCase(baseUrlPath)) {
      baseUrlPath = "";
    }

    writer.write(format("  private baseUrl: string = `${contextUrl}%s`;\n\n", baseUrlPath));

    writer.write("  constructor(private http: HttpClient) {}\n\n");

    for (Map.Entry<String, RestMethod> entry : getMethods().entrySet()) {
      String methodName = entry.getKey();
      RestMethod restMethod = entry.getValue();
      FunctionType functionType = getClassDef().getMethods().get(methodName);
      boolean hasBeanParams = false;

      writer.write("  public " + methodName);
      functionType.writeNonLambda(writer);
      writer.write(" {\n");

      String path = restMethod.getPath()
          .replace("{","${encodeURIComponent(")
          .replace("}", ")}")
          .trim();

      if("/".equalsIgnoreCase(path)) {
        path = "";
      }

      // URL Template
      writer.write(format("    let urlTmpl = `${this.baseUrl}%s`;\n", path));

      // Path Params
      writer.write("    let pathParams = {\n");
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
      writer.write("    let params = {\n");

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
        for (Param param : restMethod.getParams()) {
          if (param.getType() == ParamType.BEAN) {
            writer.write(format("    for ( let key in %s ) {\n", param.getName()));
            writer.write(format("      params[key] = %s[key];\n", param.getName()));
            writer.write("    }\n");
          }
        }
      }

      writer.write("\n");

      if (restMethod.getHttpMethod() == HttpMethod.GET) {
        writer.write("    return this.http.get(urlTmpl, {\n"); // @todo change to get<T> to return observable of type T
        writer.write("      params: params,\n");
        writer.write("      responseType: 'json'\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.POST) {
        writer.write("    return this.http.post(urlTmpl, {\n"); // @todo change to get<T> to return observable of type T
        for (Param param: restMethod.getParams()) {
          if (param.getType() == ParamType.BODY){
            writer.write(format("      data: %s,\n", param.getName() ));
            break;
          }
        }
        writer.write("      params: params,\n");
        if (!(functionType.getResultType() instanceof VoidType)) {
          writer.write("      responseType: 'json'\n");
        }
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.PUT) {
        writer.write("    return this.http.put(urlTmpl, {\n"); // @todo change to get<T> to return observable of type T
        for (Param param: restMethod.getParams()) {
          if (param.getType() == ParamType.BODY){
            writer.write(format("      data: %s,\n", param.getName() ));
            break;
          }
        }
        if (!(functionType.getResultType() instanceof VoidType)) {
          writer.write("      responseType: 'json',\n");
        }
        writer.write("      params: params\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.DELETE) {
        writer.write("    return this.http.delete(urlTmpl, {\n"); // @todo change to get<T> to return observable of type T
        writer.write("      params: params,\n");
        writer.write("      responseType: 'json'\n");
        writer.write("    });\n");
      }

      writer.write("  }\n\n");
    }

    writer.write("}\n\n");
  }

}
