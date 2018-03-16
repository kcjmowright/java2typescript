package java2typescript.jackson.module.grammar.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import com.google.common.base.CaseFormat;

abstract public class AbstractNamedType extends AbstractType {

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

  public String getFileName() {
    return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, getDefName()) + ".ts";
  }

  public String getDefName() {
    return name;
  }

  public String[] getPackagePath() {
    return this.packagePath;
  }

  public void setPackagePath(String[] packagePath) {
    this.packagePath = packagePath;
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
