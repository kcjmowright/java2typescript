package java2typescript.jackson.module.grammar;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class Module extends AbstractNamedType {

  private Map<String, Module> modules = new HashMap<>();

  private Map<String, AbstractNamedType> namedTypes = new HashMap<>();

  private Map<String, AbstractType> vars = new LinkedHashMap<>();

  private boolean export = false;

  public Module(String[] packagePath, String name) {
    super(packagePath, name);
  }

  public Module(String name) {
    super(name == null ? new String[]{} : name.split("\\."), name);
  }

  public Module() {
    this(null);
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

  public void setExport(boolean export) {
    this.export = export;
  }

  public boolean isExport() {
    return this.export;
  }

  public void externalize(File baseFile) throws IOException {
    for (Module module : modules.values()) {
      module.externalize(baseFile);
    }

    for (AbstractNamedType type : namedTypes.values()) {
      type.externalize(baseFile);
    }

    File path = new File(baseFile, String.join(File.separator, packagePath));
    try (Writer writer = getWriter(path, "index.ts")) {
      writeDef(writer);
      writer.flush();
    }
  }

  public void writeDef(Writer writer) throws IOException {
    if (getModules().values().size() > 0) {
      for (Module module : getModules().values().stream().filter(m -> m.isExport()).collect(Collectors.toList())) {
        writer.write("export * from './" + String.join(File.separator, module.getPackagePath()) + "';\n");
      }
      writer.write("\n");
    }

    if (getNamedTypes().values().size() > 0) {

      for (AbstractNamedType type : getNamedTypes().values()) {
        writer.write("import { " + type.getDefName() +
            " } from './" + type.getFileName().replaceAll("\\.ts$", "") + "';\n");
      }
      writer.write("\n");

      Iterator<AbstractNamedType> namedTypes = getNamedTypes().values().iterator();

      writer.write("export {\n");
      while (namedTypes.hasNext()) {
        writer.write("  " + namedTypes.next().getDefName());
        if (namedTypes.hasNext()) {
          writer.write(",");
        }
        writer.write("\n");
      }
      writer.write("};\n\n");

      namedTypes = getNamedTypes().values().stream().filter(ant -> !ant.getFileName().startsWith("i-")).iterator();
      writer.write("export const " + getName().toUpperCase().replaceAll("\\.", "_") + "_PROVIDERS = [\n");
      while (namedTypes.hasNext()) {
        writer.write("  " + namedTypes.next().getDefName());
        if (namedTypes.hasNext()) {
          writer.write(",");
        }
        writer.write("\n");
      }
      writer.write("];\n\n");
    }

    if (getVars().values().size() > 0) {
      for (AbstractType type : getVars().values()) {
        type.write(writer);
        writer.write("\n");
      }
      writer.write("\n");
    }
  }

}
