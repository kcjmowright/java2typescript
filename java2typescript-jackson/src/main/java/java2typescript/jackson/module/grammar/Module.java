package java2typescript.jackson.module.grammar;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class Module extends AbstractNamedType {

  private Map<String, Module> modules = new HashMap<>();

  private Map<String, AbstractNamedType> namedTypes = new HashMap<>();

  private Map<String, AbstractType> vars = new LinkedHashMap<>();

  public Module(String name) {
    super(new String[] { "" }, name);
  }

  public Map<String, AbstractNamedType> getNamedTypes() {
    return namedTypes;
  }

  public Map<String, AbstractType> getVars() {
    return vars;
  }

  public Map<String, Module> getModules() {
    return modules;
  }

  public String getName() {
    return name;
  }

  public void externalize(File baseFile) throws IOException {

    for (Module module : modules.values()) {
      module.externalize(baseFile);
    }

    for (AbstractNamedType type : namedTypes.values()) {
      type.externalize(baseFile);
    }

    File path = new File(baseFile, String.join(File.pathSeparator, packagePath));
    try (Writer writer = getWriter(path, "index.ts")) {
      writeDef(writer);
      writer.flush();
    }
  }

  public void writeDef(Writer writer) throws IOException {
    for (Module module : modules.values()) {
      writer.write("export * from '" + String.join(File.separator, module.getPackagePath()) + "';\n");
    }
    writer.write("\n");

    for (AbstractNamedType type : namedTypes.values()) {
      writer.write("export { " + type.getDefName() + " } from './" + String.join(File.separator, type.getPackagePath()) + "';\n");
    }
    writer.write("\n");

    for (AbstractType type : getVars().values()) {
      type.write(writer);
      writer.write("\n");
    }
    writer.write("\n");
  }

}
