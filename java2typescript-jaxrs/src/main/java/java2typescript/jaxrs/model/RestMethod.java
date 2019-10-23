package java2typescript.jaxrs.model;

import static java.lang.String.format;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.AnyType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.BooleanType;
import java2typescript.jackson.module.grammar.FunctionType;
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
  private MediaType producesContentType = MediaType.JSON;
  private MediaType consumesContentType = MediaType.JSON;

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

  public void setProducesContentType(MediaType mediaType) {
    this.producesContentType = mediaType;
  }

  public MediaType getProducesContentType() {
    return this.producesContentType;
  }

  public void setConsumesContentType(MediaType mediaType) {
    this.consumesContentType = mediaType;
  }

  public MediaType getConsumesContentType() {
    return this.consumesContentType;
  }

  public void write(FunctionType functionType, Writer writer, String basePath, boolean isPublic) {
    try {
      String visibility = isPublic ? "public" : "private";
      // Method signature
      writer.write("  " + visibility + " " + getName());
      if (getHttpMethod() == HttpMethod.DELETE || MediaType.TEXT.equals(getProducesContentType())) {
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
        if (param.getType() == ParamType.QUERY || param.getType() == ParamType.FORM) {
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

      HttpMethod httpMethod = getHttpMethod();
      if (httpMethod == HttpMethod.GET) {
        writeGet(writer, functionType);
      } else if (httpMethod == HttpMethod.POST) {
        writePutOrPost(writer, functionType, false);
      } else if (httpMethod == HttpMethod.PUT) {
        writePutOrPost(writer, functionType, true);
      } else if (httpMethod == HttpMethod.DELETE) {
        writeDelete(writer);
      } else {
        throw new IllegalStateException(String.format("Unsupported HttpMethod %s", httpMethod));
      }

      writer.write("  }\n\n");
    } catch (CloneNotSupportedException| IOException e) {
      throw new RuntimeException(e);
    }

  }

  private void writeGet(Writer writer, FunctionType functionType) throws IOException {
    if (MediaType.JSON.equals(getProducesContentType()) ||
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
    } else if (MediaType.TEXT.equals(getProducesContentType())) {
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

  private void writePutOrPost(Writer writer, FunctionType functionType, boolean put) throws IOException {
    MediaType producesType = getProducesContentType();
    MediaType consumesType = getConsumesContentType();
    String putBody = evaluateBody();
    String returnType;
    if (MediaType.JSON.equals(producesType) || MediaType.XML.equals(producesType)) {
      returnType = ((AngularObservableType) functionType.getResultType()).getType().toJS();
    } else if (MediaType.TEXT.equals(producesType)) {
      returnType = "any";
    } else {
      returnType = "Blob";
    }

    writer.write(String.format("    return this.http.%s<%s>(urlTmpl, %s, {", put ? "put" : "post", returnType, putBody));
    writer.write("\n      params: params,");
    writer.write("\n      headers: {");
    writer.write(String.format("\n        Accept: '%s',", producesType.getMime()));
    writer.write(String.format("\n        'Content-Type': '%s'", consumesType.getMime()));
    writer.write("\n      }");
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
