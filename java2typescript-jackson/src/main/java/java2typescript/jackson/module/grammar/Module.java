package java2typescript.jackson.module.grammar;

import static java.util.stream.Collectors.toMap;

import static java2typescript.jackson.module.grammar.base.JavascriptReservedWords.sanitizeAll;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class Module extends AbstractNamedType {

  private Map<String, Module> modules = new LinkedHashMap<>();

  private Map<String, AbstractNamedType> namedTypes = new LinkedHashMap<>();

  private Map<String, AbstractType> vars = new LinkedHashMap<>();

  private boolean export = true;

  public Module(String name) {
    super(nameToPackagePath(name), cleanseName(name));
  }

  private static String[] nameToPackagePath(String name) {
    return (name == null) ? new String[]{} : name.split("\\.");
  }

  private static String cleanseName(String name) {
    return String.join(".", sanitizeAll(nameToPackagePath(name)));
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
      List<Module> sortedModules = new ArrayList<>(getModules().values().stream().filter(m -> m.isExport()).collect(Collectors.toSet()));
      Collections.sort(sortedModules, Comparator.comparing(Module::getFullyQualifiedName));

      for (Module module : sortedModules) {
        writer.write("import * as " + module.getDefName() + " from './" +
            module.getPackagePath()[module.getPackagePath().length - 1] + "';\n");
      }
      writer.write("\nexport {\n");
      int row = 0;
      for (Module module : sortedModules) {
        if (row > 0) {
          writer.write(",\n");
        }
        writer.write("  " + module.getDefName());
        row++;
      }
      writer.write("\n};\n");
    }

    if (getNamedTypes().values().size() > 0) {
      Collection<AbstractNamedType> uniqueNamedTypes = getNamedTypes().values().stream()
          .collect(toMap(v -> v.getDefName(), Function.identity(), (i1, i2) -> i1)).values();
      List<AbstractNamedType> sortedNameTypes = new ArrayList<>(uniqueNamedTypes);
      Collections.sort(sortedNameTypes, Comparator.comparing(AbstractNamedType::getDefName));

      for (AbstractNamedType type : sortedNameTypes) {
        writer.write("import { " + type.getDefName() +
            " } from './" + type.getFileName().replaceAll("\\.ts$", "") + "';\n");
      }
      writer.write("\n");

      Iterator<AbstractNamedType> namedTypes = sortedNameTypes.iterator();

      writer.write("export {\n");
      while (namedTypes.hasNext()) {
        writer.write("  " + namedTypes.next().getDefName());
        if (namedTypes.hasNext()) {
          writer.write(",");
        }
        writer.write("\n");
      }
      writer.write("};\n\n");
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
