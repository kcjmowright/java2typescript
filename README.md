# Purpose

This **maven** plugin is used to generate a typescript definition and [Angular](https://angular.io) implementation of each of the REST services and 
corresponding DTO models out of the [JAX-RS](https://jax-rs-spec.java.net/) annotated Java services.  For each Java file, the plugin generates
 a corresponding `.ts` file using the same folder structure the Java uses.  Each `.ts` file is imported into a submodule, `index.ts` file.
Each submodule is then exported from the main `index.html` file barrel style.

# Goals

There is a single goal, `generate`, that generates `.ts` files (one per java class) in their respective packages/folders under 
`tsOutPath`.

```
mvn java2typescript:generate
```

# Parameters


| Name               | Default value              | Description                          |
|-------------------:|:--------------------------:|:-------------------------------------|
| restServiceBaseDir |  -                         | Class of REST service                |
| moduleName         |  -                         | Name of output parent module         |
| contextUrl         |  -                         | The base context URL of the REST API |
| tsOutPath          | ${project.build.directory} | Path to output folder for ts file    |


# Setup

Add something like the following to your REST interface module's pom.xml:

```
    <build>
        <plugins>
            <plugin>
                <groupId>java2typescript</groupId>
                <artifactId>java2typescript-maven-plugin</artifactId>
                <version>2.0.0-SNAPSHOT</version>
                <configuration>
                    <tsOutPath>${project.build.directory}/generated/typescript</tsOutPath>
                    <restServiceBaseDir>src/main/java/com/my/rest</restServiceBaseDir>
                    <moduleName>my</moduleName>
                    <contextUrl>/myrest/api</contextUrl>
                </configuration>
            </plugin>
        </plugins>
    </build>
```    

Add the following to your ~/.m2/settings.xml, otherwise you have to use the fully qualified syntax to invoke the plugin 

```
    <pluginGroups> 
      <pluginGroup>java2typescript</pluginGroup> 
    </pluginGroups>
```


### Debugging Tips

https://stackoverflow.com/questions/2973888/debug-a-maven-plugins-execution-in-a-maven-web-project
