# Purpose

This **maven** plugin is used to generate typescript definitions and [Angular](https://angular.io) implementations of each of the REST service and 
corresponding DTO models found for a given project using the [JAX-RS](https://jax-rs-spec.java.net/) annotated Java services.
For each Java file, the plugin generates a corresponding `.ts` file using the same folder structure the Java uses.
Each `.ts` file is imported into a submodule, `index.ts` file.  Each submodule is then exported from the main `index.ts` file barrel style.

The code generated targets the Angular 7 API.

# Goals

There is a single goal, `generate`, that generates `.ts` files (one per java class) in their respective packages/folders under 
`tsOutPath`.

```
mvn java2typescript:generate
```

# Parameters


| Name               | Default value              | Description                          |
|-------------------:|:--------------------------:|:-------------------------------------|
| restServiceBaseDir |                            | Array of file paths                  |
| prefix             |  -                         | Prefix to add to exported interfaces and REST resource classes.        |
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
                    <prefix>My</prefix>
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
