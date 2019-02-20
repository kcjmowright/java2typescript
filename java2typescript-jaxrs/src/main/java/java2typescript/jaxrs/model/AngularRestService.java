package java2typescript.jaxrs.model;

import java2typescript.jackson.module.PathResolver;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

public class AngularRestService extends BaseModel {

  private String path;
  private ClassType classDef;
  private String contextToken;
  private ServerUrlContextService serverUrlContextService;
  private final Map<String, List<RestMethod>> methods = new HashMap<>();

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

  public Map<String, List<RestMethod>> getRestMethods() {
    return methods;
  }

  public void addRestMethod(RestMethod restMethod) {
    if (restMethod == null) {
      return;
    }
    methods.computeIfAbsent(restMethod.getName(), k -> new ArrayList<>()).add(restMethod);
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

    List<Map.Entry<String, List<RestMethod>>> entries =
        getRestMethods().entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(toList());
    for (Map.Entry<String, List<RestMethod>> entry: entries) {
      String methodName = entry.getKey();
      List<RestMethod> restMethods = entry.getValue();
      List<FunctionType> functionTypes = getClassDef().getMethods().get(methodName);

      if (restMethods.size() > 1) {
        OverloadedRestMethod overloadedRestMethod = new OverloadedRestMethod(restMethods, functionTypes, basePath);
        overloadedRestMethod.write(writer);
      } else {
        restMethods.get(0).write(functionTypes.get(0), writer, basePath, true);
      }
    }
    writer.write("}\n\n");
  }


}
