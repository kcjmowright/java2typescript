package java2typescript.jaxrs.model;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;

import java.io.IOException;
import java.io.Writer;

public class ServerUrlContextService extends AbstractNamedType {
  private String contextUrl;
  private String contextToken;

  public ServerUrlContextService(String[] packagePath, String contextUrl, String contextToken) {
    super(packagePath, contextToken == null ? "SERVER_URL_CONTEXT" : contextToken);
    this.contextUrl = contextUrl == null ? "" : contextUrl;
    this.contextToken = contextToken == null ? "SERVER_URL_CONTEXT" : contextToken;
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write("import { InjectionToken } from '@angular/core';\n\n");
    writer.write("export const " + contextToken + " = new InjectionToken<String>('URL Context token', {\n");
    writer.write("    providedIn: 'root',\n");
    writer.write("    factory: () => '" + contextUrl + "'\n");
    writer.write("});\n\n");
  }
}
