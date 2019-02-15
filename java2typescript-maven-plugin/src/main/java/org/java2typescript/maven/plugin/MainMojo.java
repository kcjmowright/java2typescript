package org.java2typescript.maven.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java2typescript.jackson.module.grammar.Module;
import java2typescript.jaxrs.ServiceDescriptorGenerator;

import com.google.common.base.CaseFormat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Generate Typescript from REST service interfaces.
 *
 */
@Mojo(
    name = "generate",
    defaultPhase = LifecyclePhase.PROCESS_CLASSES,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class MainMojo extends AbstractMojo {


  private static final String packageRegex = "^package\\s+([^;]+);$";
  private static final Pattern packagePattern = Pattern.compile(packageRegex);
  private static final String classRegex = "^public\\s+interface\\s+\\w+\\s+\\{\\s*$";


  /**
   * REST service interface directory
   */
  @Parameter(alias = "restServiceBaseDir", required = true)
  private String[] restServiceBaseDir;

  /**
   * Prefix to use when naming resource class implementations.
   */
  @Parameter(alias = "prefix", required = true)
  private String prefix;

  /**
   * The context URL
   */
  @Parameter(alias = "contextUrl", required = true)
  private String contextUrl;


  /**
   * The context URL
   */
  @Parameter(alias = "contextToken", required = true)
  private String contextToken;

  /**
   * Path to output typescript folder
   */
  @Parameter(alias = "tsOutPath", defaultValue = "${project.build.directory}", required = true)
  private File tsOutPath;

  /**
   *
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;

  /**
   *
   * @param f the file
   * @return
   * @throws Exception
   */
  public String readClassName(File f) throws Exception {
    String fileName = f.getName();
    getLog().info("Reading class name for file " + f.getAbsolutePath());
    final String fileClassName = fileName.substring(0, fileName.length() - 5);
    final Path path = Paths.get(f.getAbsolutePath());
    final Predicate<String> matchesClassRegex = (s) -> s.matches(classRegex);
    final Predicate<String> matchesFileName = (s) -> s.contains(fileClassName);
    String className = null;

    if (Files.lines(path).anyMatch(matchesClassRegex.and(matchesFileName))) {
      final String packageName = Files.lines(path).filter(s -> s.matches(packageRegex)).findFirst().get();
      final Matcher packageMatcher = packagePattern.matcher(packageName);
      if (packageMatcher.matches()) {
        className = packageMatcher.group(1) + "." + fileClassName;
        getLog().info("Found classname : " + className);
      }
    }
    return className;
  }

  /**
   *
   * @param dir
   * @return
   * @throws Exception
   */
  public List<String> listClassNames(File dir) throws Exception {
    List<String> classNames = new ArrayList<String>();
    if (dir.isDirectory()) {
      for (File file : dir.listFiles()) {
        if (file.isDirectory()) {
          classNames.addAll(listClassNames(file));
        } else if (file.isFile() && file.getName().endsWith(".java")) {
          String className = readClassName(file);
          if (className != null) {
            classNames.add(className);
          }
        }
      }
    }
    return classNames;
  }

  /**
   *
   * @throws MojoExecutionException
   */
  @Override
  @SuppressFBWarnings()
  public void execute() throws MojoExecutionException {

    List<Class<?>> classes = new ArrayList<>();
    Set<URL> urls = new HashSet<>();

    try {

      for (String element : project.getCompileClasspathElements()) {
        urls.add(new File(element).toURI().toURL());
      }
      ClassLoader urlClassLoader = URLClassLoader.newInstance(
          urls.toArray(new URL[0]),
          Thread.currentThread().getContextClassLoader());

      for( String aRestServiceBaseDir : restServiceBaseDir) {
        File f = new File(aRestServiceBaseDir);
        if (f.isDirectory()) {
          for (String className : listClassNames(f)) {
            // Descriptor for service
            Class<?> serviceClass = urlClassLoader.loadClass(className);
            classes.add(serviceClass);
          }
        }
      }

      ServiceDescriptorGenerator descGen = new ServiceDescriptorGenerator(classes);
      Module tsModule = descGen.generateTypeScript(prefix, contextUrl, contextToken);
      tsModule.externalize(tsOutPath);

    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

}
