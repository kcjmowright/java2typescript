package java2typescript.jackson.module.grammar.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract public class AbstractNamedType extends AbstractType {

  private static final Pattern pattern = Pattern.compile("[A-Z]{2,}+|[A-Z]");

  protected final String name;

  protected String[] packagePath;

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
    String text = getDefName();
    Matcher matcher = pattern.matcher(text);
    StringBuilder sb = new StringBuilder();
    int lastIndex = 0;
    while (matcher.find()) {
      if (lastIndex > 0) {
        sb.append("-");
      }
      if (lastIndex < matcher.start()) {
        sb.append(text.substring(lastIndex, matcher.start()).toLowerCase());
        lastIndex = matcher.start();
      } else {
        sb.append(text.substring(matcher.start(), matcher.end() - 1).toLowerCase());
        lastIndex = matcher.end() - 1;
      }
    }
    if (lastIndex + 1 < text.length()) {
      if (lastIndex > 0) {
        sb.append("-");
      }
      sb.append(text.substring(lastIndex).toLowerCase());
    }
    return sb.append(".ts").toString();
  }

  public String getDefName() {
    return getSimpleName();
  }

  public String[] getPackagePath() {
    return this.packagePath;
  }

  public void setPackagePath(String[] packagePath) {
    this.packagePath = packagePath;
  }

  public String getFullyQualifiedName() {
    return String.join(".", getPackagePath()) + "." + getDefName();
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

}
