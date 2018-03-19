package java2typescript.jaxrs.model;

import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.VoidType;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class AngularRestService extends BaseModel {

  private String path;
  private String angularModule;
  private ClassType classDef;

  public AngularRestService(String[] packagePath, String name, String path, String angularModule) {
    super(packagePath, name);
    this.path = path;
    this.angularModule = angularModule;
  }

  private final Map<String, RestMethod> methods = new HashMap<>();

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

  public ClassType getClassDef() {
    return classDef;
  }

  public void setClassDef(ClassType classDef) {
    this.classDef = classDef;
  }

  public Map<String, RestMethod> getRestMethods() {
    return methods;
  }

//  @Override
//  public Map<String, FunctionType> getMethods() {
//    return new LinkedHashMap<>(methods);
//  }

  public String getContextUrlPath() {
    StringBuilder sb = new StringBuilder();
    Arrays.stream(this.packagePath).forEach(p -> sb.append("../"));
    sb.append("shared/context-url");
    return sb.toString();
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write("import { Injectable } from '@angular/core';\n");
    writer.write("import { HttpClient } from '@angular/common/http';\n");
    writer.write("import { Observable } from 'rxjs/Observable'\n\n");

    for(Map.Entry<AbstractNamedType, String> entry: getClassDef().resolveImports().entrySet()) {
      writer.write("import { " + entry.getKey().getDefName() + " } from '" + entry.getValue() + "';\n");
    }

    writer.write("import { contextUrl } from '" + getContextUrlPath() + "';\n\n");

    writer.write("@Injectable()\n");
    writer.write(format("export class %s implements %s {\n", getName(), "I" + getName()));

    String baseUrlPath = getPath().replace("{","${encodeURIComponent(").replace("}",")}").trim();

    if("/".equalsIgnoreCase(baseUrlPath)) {
      baseUrlPath = "";
    }

    writer.write("  constructor(private http: HttpClient) {}\n\n");

    for (Map.Entry<String, RestMethod> entry : getRestMethods().entrySet()) {
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
      writer.write(format("    let urlTmpl: string = `${contextUrl}%s%s`;\n", baseUrlPath, path));

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
        writer.write("    return this.http.get<");
        ((AngularObservableType)functionType.getResultType()).getType().write(writer);
        writer.write(">(urlTmpl, {\n");
        writer.write("      params: params,\n");
        writer.write("      responseType: 'json'\n");
        writer.write("    });\n");
      } else if (restMethod.getHttpMethod() == HttpMethod.POST) {
        writer.write("    return this.http.post<");
        ((AngularObservableType)functionType.getResultType()).getType().write(writer);
        writer.write(">(urlTmpl, {\n");
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
        writer.write("    return this.http.put<");
        ((AngularObservableType)functionType.getResultType()).getType().write(writer);
        writer.write(">(urlTmpl, {\n");
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
        writer.write("    return this.http.delete(urlTmpl, {\n");
        writer.write("      params: params,\n");
        writer.write("      responseType: 'json'\n");
        writer.write("    });\n");
      }

      writer.write("  }\n\n");
    }

    writer.write("}\n\n");
  }

}
