/*******************************************************************************
 * Copyright 2013 Raphael Jolivet
 * Copyright 2015 Justin Wright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package java2typescript.jaxrs;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import static java2typescript.jaxrs.model.ParamType.BEAN;
import static java2typescript.jaxrs.model.ParamType.BODY;
import static java2typescript.jaxrs.model.ParamType.FORM;
import static java2typescript.jaxrs.model.ParamType.PATH;
import static java2typescript.jaxrs.model.ParamType.QUERY;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.google.common.base.CaseFormat;

import java2typescript.jackson.module.DefinitionGenerator;
import java2typescript.jackson.module.grammar.AnyType;
import java2typescript.jaxrs.model.AngularRestService;
import java2typescript.jaxrs.model.MediaType;
import java2typescript.jaxrs.model.ServerUrlContextService;
import java2typescript.jaxrs.model.HttpMethod;
import java2typescript.jaxrs.model.Param;
import java2typescript.jaxrs.model.RestMethod;
import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.ClassType;
import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.Module;
import java2typescript.jackson.module.grammar.VoidType;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

/**
 * Generates a {@link AngularRestService} description out of a service class /
 * interface
 */
public class ServiceDescriptorGenerator {

  private final Collection<? extends Class<?>> classes;

  private ObjectMapper mapper;

  public ServiceDescriptorGenerator(Collection<? extends Class<?>> classes) {
    this(classes, new ObjectMapper());
  }

  /**
   *
   * @param classes
   * @param mapper
   */
  public ServiceDescriptorGenerator(Collection<? extends Class<?>> classes, ObjectMapper mapper) {
    this.classes = classes;
    this.mapper = mapper;
    addDummyMappingForJAXRSClasses();
  }

  /**
   *
   */
  private class DummySerializer extends JsonSerializer<Object> {
    @Override
    public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) {
      // No implementation here
    }
  }

  /**
   * Those classes will be transformed as "any"
   */
  private void addDummyMappingForJAXRSClasses() {
    SimpleModule module = new SimpleModule("dummy jax-rs mappings");
    module.addSerializer(Response.class, new DummySerializer());
    module.addSerializer(UriInfo.class, new DummySerializer());
    module.addSerializer(Request.class, new DummySerializer());
    mapper.registerModule(module);
  }

  /**
   *
   * @param classes
   * @param prefix
   * @return Collection of AngularRestService instances.
   */
  private Collection<AngularRestService> generateRestServices(Collection<? extends Class<?>> classes, String prefix, String contextUrl, String contextToken) {
    List<AngularRestService> services = new ArrayList<>();
    ServerUrlContextService serverUrlContextService = null;

    for (Class<?> clazz : classes) {
      String[] packagePath = clazz.getPackage().getName().split("\\.");
      if (serverUrlContextService == null) {
        serverUrlContextService = new ServerUrlContextService(packagePath, contextUrl, contextToken);
      }
      Path pathAnnotation = clazz.getAnnotation(Path.class);
      String pathValue = "";
      if (pathAnnotation != null) {
        pathValue = pathAnnotation.value();
      }
      AngularRestService service =
          new AngularRestService(packagePath, clazz.getSimpleName(), pathValue, prefix, contextToken, serverUrlContextService);

      for (Method method : clazz.getDeclaredMethods()) {
        if (Modifier.isPublic(method.getModifiers())) {
          RestMethod restMethod = generateMethod(method);
          service.addRestMethod(restMethod);
        }
      }
      services.add(service);
    }
    return services;
  }

  /**
   *  @param prefix prefix for naming resource class implementations.
   * @param contextUrl the context url
   * @param contextToken the name of the Angular contextToken
   */
  public Module generateTypeScript(String prefix, String contextUrl, String contextToken) throws JsonMappingException {
    DefinitionGenerator defGen = new DefinitionGenerator(mapper);
    Module mainModule = defGen.generateTypeScript(classes);

    Collection<AngularRestService> restServices = generateRestServices(classes, prefix, contextUrl, contextToken);

    for (AngularRestService restService : restServices) {
      Module module = findModule(mainModule, restService.getPackagePath());
      module.setExport(true);
      AbstractNamedType abstractNamedType = module.getNamedTypes().get(restService.getFullyQualifiedName());
      if (abstractNamedType instanceof ClassType) {
        ClassType classDef = (ClassType) abstractNamedType;
        classDef.setPrefix(prefix);
        decorateParamNames(restService, classDef);
        restService.setClassDef(classDef);
        module.getNamedTypes().put(restService.getFullyQualifiedName(), restService);
        module.getNamedTypes().put(classDef.getFullyQualifiedName(), classDef);

        if (module.getNamedTypes().get(restService.getServerUrlContextService().getFullyQualifiedName()) == null
            && String.join(".", module.getPackagePath())
              .equalsIgnoreCase(String.join(".", restService.getServerUrlContextService().getPackagePath()))) {
          module.getNamedTypes().put(restService.getServerUrlContextService().getFullyQualifiedName(), restService.getServerUrlContextService());
        }
      }
    }
    return mainModule;
  }

  public Module findModule(Module module, String[] packagePaths) {
    String targetPackage = packagePaths.length > 0 ? packagePaths[packagePaths.length - 1] : "";
    Module next = module;
    String path = "";
    for (String partialPath: packagePaths) {
      path += partialPath;
      next = next.getModules().get(path);
      if (targetPackage.equalsIgnoreCase(next.getDefName())) {
        return next;
      }
      path += ".";
    }
    return null;
  }

  /**
   *
   * @param method the Java method
   */
  private RestMethod generateMethod(Method method) {

    RestMethod restMethod = new RestMethod();
    Path pathAnnotation = method.getAnnotation(Path.class);

    restMethod.setPath(pathAnnotation == null ? "" : pathAnnotation.value());
    restMethod.setName(method.getName());
    if (method.getAnnotation(GET.class) != null) {
      restMethod.setHttpMethod(HttpMethod.GET);
    } else if (method.getAnnotation(POST.class) != null) {
      restMethod.setHttpMethod(HttpMethod.POST);
    } else if (method.getAnnotation(PUT.class) != null) {
      restMethod.setHttpMethod(HttpMethod.PUT);
    } else if (method.getAnnotation(DELETE.class) != null) {
      restMethod.setHttpMethod(HttpMethod.DELETE);
    }
    if (restMethod.getHttpMethod() == null) {
      return null;
    }
    restMethod.getParams().addAll(generateParams(method));
    Produces producesAnnotation = method.getAnnotation(Produces.class);
    if (producesAnnotation != null) {
      restMethod.setProducesContentType(MediaType.of(producesAnnotation.value()[0]));
    } else {
      restMethod.setProducesContentType(MediaType.JSON);
    }
    Consumes consumesAnnotation = method.getAnnotation(Consumes.class);
    if (consumesAnnotation != null) {
      restMethod.setConsumesContentType(MediaType.of(consumesAnnotation.value()[0]));
    } else {
      restMethod.setConsumesContentType(MediaType.JSON);
    }
    return restMethod;
  }

  /**
   *
   * @param method the Java method.
   */
  private List<Param> generateParams(Method method) {
    LinkedHashMap<String, Param> params = new LinkedHashMap<>();
    for (Parameter parameter: method.getParameters()) {
      if (parameter.getAnnotations() == null || parameter.getAnnotations().length == 0){
        Param param = new Param();
        param.setName(getInstanceName(parameter, params));
        param.setType(BODY);
        params.put(param.getName(), param);
      } else {
        for (Annotation annotation : parameter.getAnnotations()) {
          fillParam(annotation, parameter, params);
        }
      }
    }
    List<Param> paramsList = new ArrayList<>();
    paramsList.addAll(params.values());
    return paramsList;
  }

  /**
   * Parameters can have more than one annotation.  Storing the `param` under the `parameter` name
   * which doesn't necessarily match so that we'll lookup the `param` later by the `parameter` name.
   * Reflection will sometimes return arg0, arg1, etc, while the annotation value is the name we truly want to give it.
   * @param annot
   * @param parameter
   * @param params
   */
  private void fillParam(Annotation annot, Parameter parameter, LinkedHashMap<String, Param> params) {
    Param param = params.get(parameter.getName());
    if (param == null) {
      param = new Param();
      param.setName(parameter.getName());
      param.setType(BODY);
      params.put(parameter.getName(), param);
    }
    if (annot instanceof PathParam) {
      param.setType(PATH);
      param.setName(((PathParam) annot).value());
    } else if (annot instanceof QueryParam) {
      param.setType(QUERY);
      param.setName(((QueryParam) annot).value());
    } else if (annot instanceof FormParam) {
      param.setType(FORM);
      param.setName(((FormParam) annot).value());
    } else if (annot instanceof Context) {
      param.setContext(true);
    } else if (annot instanceof BeanParam) {
      param.setType(BEAN);
      param.setName(getInstanceName(parameter, params));
    }
  }

  /**
   *
   * @param parameter
   * @param params
23   * @return String the instance name.
   */
  private String getInstanceName(Parameter parameter, LinkedHashMap<String, Param> params) {
    String instanceName = parameter.getName();
    if (!parameter.getType().isPrimitive()){
      String simpleName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, parameter.getType().getSimpleName());
      int idx = 0;

      instanceName = simpleName;
      while (params.get(instanceName) != null) {
        instanceName = simpleName + ++idx;
      }
    }
    return instanceName;
  }

  /**
   *
   * @param module
   * @param classDef
   */
  private void decorateParamNames(AngularRestService module, ClassType classDef) {
    // Loop on methods of the service
    for (List<RestMethod> restMethods : module.getRestMethods().values()) {
      for (int k = 0; k < restMethods.size(); k++) {
        RestMethod restMethod = restMethods.get(k);
        FunctionType function = classDef.getMethods().get(restMethod.getName()).get(k);

        if (function == null) {
          continue;
        }
        // Copy ordered list of param types
        List<AbstractType> types = new ArrayList<>();
        if (function.getParameters() != null && function.getParameters().values() != null) {
          types.addAll(function.getParameters().values());
          function.getParameters().clear();
        }

        for (int i = 0; i < restMethod.getParams().size(); i++) {
          Param param = restMethod.getParams().get(i);
          // Skip @Context parameters
          if (!param.isContext()) {
            function.getParameters().put(param.getName(), types.get(i));
          }
        }

        if (! (function.getResultType() instanceof VoidType)) {
          function.setResultType(new AngularObservableType(function.getResultType()));
        } else {
          function.setResultType(new AngularObservableType(AnyType.getInstance()));
        }
      }
    }
  }

}
