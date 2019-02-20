package java2typescript.jackson.module.grammar.base;

import java2typescript.jackson.module.Dasherize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

abstract public class AbstractNamedType extends AbstractType {

  protected AbstractNamedType enclosingType;

  protected String name;

  protected String prefix = "";

  protected String[] packagePath;

  protected Set<AbstractNamedType> innerTypes = new HashSet<>();

  public AbstractNamedType(String[] packagePath, String className) {
    this.name = className;
    this.packagePath = packagePath;
  }

  @Override
  public void write(Writer writer) throws IOException {
    writer.write(name);
  }

  /**
   *
   * @param baseFile
   * @throws IOException
   */
  public void externalize(File baseFile) throws IOException {
    File path = new File(baseFile, String.join(File.separator, packagePath));
    try (Writer writer = getWriter(path, getFileName())) {
      writeDef(writer);
      writer.flush();
    }
  }

  public void writeDef(Writer writer) throws IOException {
    write(writer);
  }

  public String getName() {
    return name;
  }

  public String getSimpleName() {
    String[] names = name.split("\\.");
    return names[names.length - 1];
  }

  public String getFileName() {
    return this.enclosingType == null ? Dasherize.convert(getDefName()) + ".ts" : enclosingType.getFileName();
  }

  public String getDefName() {
    return getSimpleName();
  }

  public String[] getPackagePath() {
    return this.packagePath;
  }

  public Set<AbstractNamedType> getInnerTypes() {
    return innerTypes;
  }

  public String getFullyQualifiedName() {
    return String.join(".", getPackagePath()) + "." + getDefName();
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  /**
   *
   * @param folder
   * @param fileName
   * @return
   * @throws IOException
   */
  protected Writer getWriter(File folder, String fileName) throws IOException {
    folder.mkdirs();
    File file = new File(folder, fileName);
    file.createNewFile();
    FileOutputStream stream = new FileOutputStream(file);
    OutputStreamWriter writer = new OutputStreamWriter(stream, Charset.forName("UTF-8"));
    return writer;
  }

  public AbstractNamedType getEnclosingType() {
    return enclosingType;
  }

  public void setEnclosingType(AbstractNamedType enclosingType) {
    // Prefix name with enclosing class name concat with $
    this.name = String.join(".", getPackagePath()) + "." + enclosingType.getSimpleName() + "$" + this.getDefName();
    this.enclosingType = enclosingType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractNamedType that = (AbstractNamedType) o;
    return name.equals(that.name) && Arrays.equals(packagePath, that.packagePath);
  }

  @Override
  public int hashCode() {
    return 31 * Objects.hash(name) + Arrays.hashCode(packagePath);
  }
}
