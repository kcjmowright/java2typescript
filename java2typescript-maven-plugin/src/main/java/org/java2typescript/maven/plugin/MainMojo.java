package org.java2typescript.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java2typescript.jackson.module.grammar.Module;
import java2typescript.jaxrs.ServiceDescriptorGenerator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.google.common.collect.Lists;

/**
 * Generate typescript file out of RESt service definition
 * 
 * @goal generate
 * @phase process-classes
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
public class MainMojo extends AbstractMojo {

//	/**
//	 * Full class name of the REST service
//	 * @required
//	 * @parameter
//	 *    alias="serviceClass"
//	 *    expression="${j2ts.serviceClass}"
//	 */
//	private String restServiceClassName;

  	/**
  	 * REST service interface directory
  	 * @required
  	 * @parameter
  	 *    alias="serviceClass"
  	 *    expression="${j2ts.serviceClass}"
  	 */
  private String restServiceBaseDir;

	/**
	 * Name of output module (ts,js)
	 * @required 
	 * @parameter
	 *     alias="moduleName" 
	 *     expression="${j2ts.moduleName}"
	 */
	private String moduleName;

	/**
	 * Path to output typescript folder
	 * The name will be <moduleName>.d.ts
	 * @required
	 * @parameter 
	 *    alias="tsOutFolder" 
	 * 		expression="${j2ts.tsOutFolder}" 
	 * 		default-value = "${project.build.directory}"
	 */
	private File tsOutFolder;

	/**
	 * Path to output Js file
	 * The name will be <moduleName>.js
	 * 
	 * @required
	 * @parameter 
	 *    alias="jsOutFolder"
	 * 		expression="${j2ts.jsOutFolder}" 
	 * 		default-value = "${project.build.directory}"
	 */
	private File jsOutFolder;


  private static final String packageRegex = "^package\\s+([^;]+);$";
  private static final Pattern packagePattern = Pattern.compile(packageRegex);

  private static final String classRegex = "^public\\s+interface\\s+\\w+\\s+\\{\\s*$";
  private static final Pattern classPattern = Pattern.compile(classRegex);

  //private static final String re

  public String readClassName(File f) throws Exception {
    System.out.println("Reading class name for file " + f.getAbsolutePath());

    String fileName = f.getName();
    final String fileClassName = fileName.substring(0, fileName.length() - 5);
    final Path path = Paths.get(f.getAbsolutePath());
    final Predicate<String> matchesClassRegex = (s) -> s.matches(classRegex);
    final Predicate<String> matchesFileName = (s) -> s.contains(fileClassName);
    String className = null;

    if(Files.lines(path).anyMatch(matchesClassRegex.and(matchesFileName))){
      final String packageName = Files.lines(path).filter(s -> s.matches(packageRegex)).findFirst().get();
      final Matcher packageMatcher = packagePattern.matcher(packageName);
      if(packageMatcher.matches()){
        className = packageMatcher.group(1) + "." + fileClassName;
        System.out.println("Found classname : " + className);
      }
    }
    return className;
  }


  public List<String> listClassNames(File dir) throws Exception{
    List<String> classNames = new ArrayList<String>();
    if(dir.isDirectory()){
      for(File file: dir.listFiles()){
        if(file.isDirectory()){
          classNames.addAll(listClassNames(file));
        } else if(file.isFile() && file.getName().endsWith(".java")){
          String className = readClassName(file);
          if(className != null){
            classNames.add(className);
          }
        }
      }
    }
    return classNames;
  }


	@Override
	public void execute() throws MojoExecutionException {

		try {

      File f = new File(restServiceBaseDir);
      if(f.isDirectory()){
        for(String className : listClassNames(f)){
          // Descriptor for service
          Class<?> serviceClass = Class.forName(className);
          ServiceDescriptorGenerator descGen = new ServiceDescriptorGenerator(Lists.newArrayList(serviceClass));

          // To Typescript
          try(Writer writer = createFileAndGetWriter(tsOutFolder, className + ".d.ts")){
            Module tsModule = descGen.generateTypeScript(className);
            tsModule.write(writer);
          }

          // To JS
          try(Writer outFileWriter = createFileAndGetWriter(jsOutFolder, className + ".js")){
            descGen.generateJavascript(className, outFileWriter);
            outFileWriter.close();
          }
        }
      }

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private Writer createFileAndGetWriter(File folder, String fileName) throws IOException {
    folder.mkdirs();
		File file = new File(folder, fileName);
		getLog().info("Create file : " + file.getCanonicalPath());
		file.createNewFile();
		FileOutputStream stream = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		return writer;
	};
}
