package java2typescript.jaxrs.model;

import java2typescript.jackson.module.PathResolver;
import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.BooleanType;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.VoidType;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

public class AngularRestService extends BaseModel {

  private String path;
  private ClassType classDef;
  private String contextToken;
  private ServerUrlContextService serverUrlContextService;
  private final Map<String, RestMethod> methods = new HashMap<>();

  public AngularRestService(String[] packagePath, String name, String path, String prefix, String contextToken, ServerUrlContextService serverUrlContextService) {
    super(packagePath, name);
    this.contextToken = contextToken;
    this.path = path;
    this.prefix = prefix;
    this.serverUrlContextService = serverUrlContextService;
  }

  public String getPath() {
    return path;
  }

  public ServerUrlContextService getServerUrlContextService() {
    return serverUrlContextService;
  }

  @Override
  public String getDefName() {
    return getPrefix() + getSimpleName();
  }

  @Override
  public String getFullyQualifiedName() {
    return String.join(".", getPackagePath()) + "." + getSimpleName();
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

  @Override
  public void write(Writer writer) throws IOException {
    PathResolver pathResolver = PathResolver.getResolver();

    writer.write("import { Inject } from '@angular/core';\n");
    writer.write("import { Injectable } from '@angular/core';\n");
    writer.write("import { HttpClient } from '@angular/common/http';\n");
    writer.write("import { " +  serverUrlContextService.getDefName() + " } from '" +
        pathResolver.resolveImportsPath(getPackagePath(), serverUrlContextService.getPackagePath()) +
        "/" + serverUrlContextService.getFileName().replaceAll("\\.ts$", "") + "';\n");
    writer.write("import { " + getClassDef().getDefName() + " } from './" +
        getClassDef().getFileName().replaceAll("\\.ts$", "") + "';\n");

    getClassDef().resolveImports().entrySet().stream().sorted(Map.Entry.comparingByKey(
        Comparator.comparing(AbstractNamedType::getDefName))).forEach( entry -> {
          try {
            writer.write("import { " + entry.getKey().getDefName() + " } from '" + entry.getValue() + "';\n");
          } catch(IOException e) {
            throw new RuntimeException(e);
          }
    });

    writer.write("\n@Injectable({\n  providedIn: 'root'\n})\n");
    writer.write(format("export class %s implements %s {\n", getDefName(), getClassDef().getDefName()));

    String baseUrlPath = getPath().replace("{","${encodeURIComponent(pathParams.")
        .replace("}",")}").trim();

    if (!baseUrlPath.startsWith("/")) {
      baseUrlPath = "/" + baseUrlPath;
    }
    if ("/".equalsIgnoreCase(baseUrlPath)) {
      baseUrlPath = "";
    }
    final String basePath = baseUrlPath;

    writer.write("  constructor(private http: HttpClient, @Inject(");
    writer.write(this.contextToken);
    writer.write(") private context: string) {}\n\n");

    getRestMethods().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
      try {
        String methodName = entry.getKey();
        RestMethod restMethod = entry.getValue();
        FunctionType functionType = getClassDef().getMethods().get(methodName);
        boolean hasBeanParams = restMethod.getParams().stream().anyMatch(p -> p.getType() == ParamType.BEAN);
        writer.write("  public " + methodName);
        functionType.writeNonLambda(writer);
        writer.write(" {\n");

        // Path Params
        writer.write("    const pathParams = {\n");
        int pathParamCount = 0;
        for ( Param param: restMethod.getParams()) {
          if (param.getType() == ParamType.PATH) {
            if (pathParamCount++ > 0) {
              writer.write(",\n");
            }
            writer.write(format("      %s: '' + %s", param.getName(), param.getName()));
          } else if (param.getType() == ParamType.BEAN) {
            hasBeanParams = true;
          }
        }
        writer.write("\n    };\n");
        // Query Params
        writer.write("    const params = {\n");

        int queryParamCount = 0;
        for (Param param : restMethod.getParams()) {
          if (param.getType() == ParamType.QUERY) {
            if (queryParamCount++ > 0) {
              writer.write(",\n");
            }
            AbstractType paramType = functionType.getParameters().get(param.getName());
            if (paramType instanceof ArrayType) {
              writer.write(format("        %s: %s.map(v => '' + v)", param.getName(), param.getName()));
            } else {
              writer.write(format("        %s: '' + %s", param.getName(), param.getName()));
            }
          }
        }
        writer.write("\n    };\n");

        // Bean Params
        if (hasBeanParams) {
          for (Param param : restMethod.getParams()) {
            if (param.getType() == ParamType.BEAN) {
              writer.write(format("    for ( const key in %s ) {\n", param.getName()));
              writer.write("      if (key !== undefined && key !== null) {\n");
              writer.write(format("        params[key] = %s[key];\n", param.getName()));
              writer.write("      }\n");
              writer.write("    }\n");
            }
          }
        }

        writer.write("\n");

        String path = restMethod.getPath()
            .replace("{","${encodeURIComponent(pathParams.")
            .replace("}", ")}")
            .trim();

        if (!path.startsWith("/")) {
          path = "/" + path;
        }
        if ("/".equalsIgnoreCase(path)) {
          path = "";
        }
        writer.write(format("    const urlTmpl = `${this.context}%s%s`;\n\n", basePath, path));
        if (restMethod.getHttpMethod() == HttpMethod.GET) {
          if ("application/json".equalsIgnoreCase(restMethod.getProducesContentType()) ||
              ((AngularObservableType)functionType.getResultType()).getType() instanceof BooleanType) {
            writer.write("    return this.http.get<");
            ((AngularObservableType)functionType.getResultType()).getType().write(writer);
            writer.write(">(urlTmpl, {\n");
            writer.write("      params: params,\n");
            writer.write("      responseType: 'json'\n");
          } else {
            writer.write("    return this.http.get(urlTmpl, {\n");
            writer.write("      params: params,\n");
            writer.write("      responseType: 'blob'\n");
          }
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
            if ("application/json".equalsIgnoreCase(restMethod.getProducesContentType())) {
              writer.write("      responseType: 'json'\n");
            } else {
              writer.write("      responseType: 'blob'\n");
            }
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
            if ("application/json".equalsIgnoreCase(restMethod.getProducesContentType())) {
              writer.write("      responseType: 'json',\n");
            } else {
              writer.write("      responseType: 'blob',\n");
            }
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
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });

    writer.write("}\n\n");
  }

}
