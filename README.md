# Purpose

This **maven** plugin is used to generate a typescript definition and [Angular](http://angularjs.org) implementation of REST services and 
corresponding DTO model out of [JAX-RS](https://jax-rs-spec.java.net/) annotated Java services.

# Goals

There is a single goal **generate** that generates a `.ts` file.

```
mvn java2typescript:generate
```

# Parameters


| Name               | Default value              | Description                          |
|-------------------:|:--------------------------:|:-------------------------------------|
| restServiceBaseDir |  -                         | Class of REST service                |
| moduleName         |  -                         | Name of output parent module         |
| subModuleName      |  -                         | Name of output sub module            |
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
                <version>1.0.0-SNAPSHOT</version>
                <configuration>
                    <tsOutPath>${project.build.directory}/generated/typescript</tsOutPath>
                    <restServiceBaseDir>src/main/java/com/my/rest</restServiceBaseDir>
                    <moduleName>my</moduleName>
                    <subModuleName>myrest</subModuleName>
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
