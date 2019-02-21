package java2typescript.jaxrs.model;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.AnyType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.BooleanType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.VoidType;
import java2typescript.jackson.module.grammar.base.AbstractType;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestMethod extends FunctionType {

  private String name;
  private String path;
  private List<Param> params = new ArrayList();
  private Map<String, Param> pathParams = new LinkedHashMap<>();
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

  public void write(FunctionType functionType, Writer writer, String basePath, boolean isPublic) {
    try {
      String visibility = isPublic ? "public" : "private";
      // Method signature
      writer.write("  " + visibility + " " + getName());
      if (getHttpMethod() == HttpMethod.DELETE || "text/plain".equalsIgnoreCase(getProducesContentType())) {
        functionType = (FunctionType)functionType.clone();
        functionType.setResultType(AnyType.getInstance());
      }
      functionType.writeNonLambda(writer);
      writer.write(" {\n");

      // Path Params
      boolean hasBeanParams = getParams().stream().anyMatch(p -> p.getType() == ParamType.BEAN);
      writer.write("    const pathParams = {\n");
      int pathParamCount = 0;
      for ( Param param: getParams()) {
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
      for (Param param : getParams()) {
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
        for (Param param : getParams()) {
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

      String path = getPath()
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

      if (getHttpMethod() == HttpMethod.GET) {
        writeGet(writer, functionType);
      } else if (getHttpMethod() == HttpMethod.POST) {
        writePost(writer, functionType);
      } else if (getHttpMethod() == HttpMethod.PUT) {
        writePut(writer, functionType);
      } else if (getHttpMethod() == HttpMethod.DELETE) {
        writeDelete(writer);
      }

      writer.write("  }\n\n");
    } catch (CloneNotSupportedException| IOException e) {
      throw new RuntimeException(e);
    }

  }

  private void writeGet(Writer writer, FunctionType functionType) throws IOException {
    if ("application/json".equalsIgnoreCase(getProducesContentType()) ||
        ( functionType.getResultType() instanceof AngularObservableType && (((AngularObservableType)functionType.getResultType()).getType() instanceof BooleanType))) {
      writer.write("    return this.http.get<");
      AbstractType resultType = functionType.getResultType();
      if (resultType instanceof AngularObservableType) {
        ((AngularObservableType) functionType.getResultType()).getType().write(writer);
      } else {
        resultType.write(writer);
      }
      writer.write(">(urlTmpl, {\n");
      writer.write("      params: params,\n");
      writer.write("      headers: {\n");
      writer.write("        Accept: 'application/json'\n");
      writer.write("      },\n");
      writer.write("      responseType: 'json'\n");
    } else if ("text/plain".equalsIgnoreCase(getProducesContentType())) {
      writer.write("    return this.http.get(urlTmpl, {\n");
      writer.write("      params: params,\n");
      writer.write("      responseType: 'text'\n");
    } else {
      writer.write("    return this.http.get(urlTmpl, {\n");
      writer.write("      params: params,\n");
      writer.write("      responseType: 'blob'\n");
    }
    writer.write("    });\n");
  }

  private void writePost(Writer writer, FunctionType functionType) throws IOException {
    writer.write("    return this.http.post<");
    ((AngularObservableType)functionType.getResultType()).getType().write(writer);
    writer.write(">(urlTmpl, ");

    String postBody = evaluateBody();

    writer.write(format("%s, {\n      params: params,\n", postBody));

    if (!(functionType.getResultType() instanceof VoidType)) {
      if ("application/json".equalsIgnoreCase(getProducesContentType())) {
        writer.write("      headers: {\n");
        writer.write("        Accept: 'application/json'\n");
        writer.write("      },\n");
        writer.write("      responseType: 'json'\n");
      } else {
        writer.write("      responseType: 'blob'\n");
      }
    }
    writer.write("    });\n");
  }

  private void writePut(Writer writer, FunctionType functionType) throws IOException {
    boolean isJson = "application/json".equalsIgnoreCase(getProducesContentType());
    boolean isXml = "application/xml".equalsIgnoreCase(getProducesContentType());
    boolean isText = "text/plain".equalsIgnoreCase(getProducesContentType());

    writer.write("    return this.http.put<");
    if (isJson || isXml) {
      ((AngularObservableType) functionType.getResultType()).getType().write(writer);
    } else if (isText) {
      writer.write("any");
    } else {
      writer.write("Blob");
    }
    writer.write(">(urlTmpl, ");

    String putBody = evaluateBody();

    writer.write(format("%s, {\n      params: params", putBody));
    if (!(functionType.getResultType() instanceof VoidType)) {
      if (isJson) {
        writer.write(",\n      headers: {\n");
        writer.write("        Accept: 'application/json'\n");
        writer.write("      },\n");
        writer.write("      responseType: 'json'");
      }
    }
    writer.write("\n    });\n");
  }

  private void writeDelete(Writer writer) throws IOException {
    writer.write("    return this.http.delete(urlTmpl, {\n");
    writer.write("      params: params,\n");
    writer.write("      responseType: 'json'\n");
    writer.write("    });\n");
  }

  private String evaluateBody() {
    String putBody = "null";

    for (Param param: getParams()) {
      if (param.getType() == ParamType.BODY){
        putBody = param.getName();
        break;
      }
    }
    return putBody;
  }

}
