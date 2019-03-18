package java2typescript.jaxrs.model;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import java2typescript.jackson.module.grammar.FunctionType;
import java2typescript.jackson.module.grammar.FunctionTypeComparator;
import java2typescript.jackson.module.grammar.base.AbstractPrimitiveType;
import java2typescript.jackson.module.grammar.base.AbstractType;

public class OverloadedRestMethod extends AbstractType {

  private List<FunctionType> functionTypes;
  private List<RestMethod> restMethods;
  private String basePath;
  private String methodName;
  private List<String> parameterNames = new ArrayList<>();
  private Set<AbstractType> resultTypes = new HashSet<>();

  private Map<FunctionType, RestMethod> functionTypesToRestMethodMap = new HashMap<>();
  private Map<FunctionType, RestMethod> functionTypesToPrivateRestMethodMap = new HashMap<>();

  private List<Set<AbstractType>> functionsParameters = new ArrayList<>();
  private List<Set<String>> functionsParameterNames = new ArrayList<>();
  private List<Set<Param>> restParams = new ArrayList<>();

  private AtomicInteger counter = new AtomicInteger(0);

  public OverloadedRestMethod(List<RestMethod> restMethods, List<FunctionType> functionTypes, String basePath) {
    this.restMethods = restMethods;
    this.functionTypes = functionTypes;
    this.basePath = basePath;
    this.methodName = restMethods.get(0).getName();
    process();
  }

  private void process() {
    for (int i = 0; i < functionTypes.size(); i++) {
      functionTypesToRestMethodMap.put(functionTypes.get(i), restMethods.get(i));
    }

    functionTypes = functionTypes.stream().sorted(new FunctionTypeComparator()).collect(toList());

    for (FunctionType functionType: functionTypes) {
      addParametersToCollection(functionType.getParameters().entrySet(), functionsParameterNames, functionsParameters);
      resultTypes.add(functionType.getResultType());

      RestMethod restMethod = functionTypesToRestMethodMap.get(functionType);
      addRestParamsToCollection(restMethod.getParams(), restParams);
      functionTypesToPrivateRestMethodMap.put(functionType, cloneRestMethod(restMethod.getName() + counter.getAndIncrement(), restMethod));
    }

    for (int paramIndex = 0; paramIndex < functionsParameters.size(); paramIndex++) {
      Set<String> overloadedParamNames = functionsParameterNames
          .get(paramIndex)
          .stream()
          .map(name -> name.replaceFirst("^" + name.charAt(0), Character.toString(Character.toUpperCase(name.charAt(0)))))
          .collect(toSet());
      String paramName = String.join("Or", overloadedParamNames).trim();
      String replaceRegex = "^" + paramName.charAt(0);
      String replacement =  Character.toString(Character.toLowerCase(paramName.charAt(0)));
      paramName = paramName.replaceFirst(replaceRegex, replacement);
      parameterNames.add(paramName);
    }

  }

  private RestMethod cloneRestMethod(String name, RestMethod restMethod) {
    RestMethod cloned = null;
    try {
      cloned = (RestMethod)restMethod.clone();
      cloned.setName(name);
    } catch(CloneNotSupportedException ex) {
      ex.printStackTrace();
    }
    return cloned;
  }

  private void addParametersToCollection(Collection<Map.Entry<String, AbstractType>> parameters, List<Set<String>> parameterNames, List<Set<AbstractType>> collection) {
    int i = 0;
    for (Map.Entry<String, AbstractType> entry: parameters) {
      Set<AbstractType> parameter;
      Set<String> name;
      AbstractType param = entry.getValue();
      String paramName = entry.getKey();

      if (i >= collection.size()) {
        parameter = new LinkedHashSet<>();
        name = new LinkedHashSet<>();
        collection.add(parameter);
        parameterNames.add(name);
      } else {
        parameter = collection.get(i);
        name = parameterNames.get(i);
      }
      parameter.add(param);
      name.add(paramName);
      i++;
    }
  }

  private void addRestParamsToCollection(List<Param> params, List<Set<Param>> collection) {
    int i = 0;
    for (Param param: params) {
      Set<Param> parameter;

      if (i >= collection.size()) {
        parameter = new LinkedHashSet<>();
        collection.add(parameter);
      } else {
        parameter = collection.get(i);
      }
      parameter.add(param);
      i++;
    }
  }

  private void writeOverloadedMethod(Writer writer) throws IOException {
      writer.write("  public " + methodName);
      writer.write("(");

      boolean first = true;
      for (int paramIndex = 0; paramIndex < functionsParameters.size(); paramIndex++) {
        if (!first) {
          writer.write(", ");
        }

        writer.write(parameterNames.get(paramIndex));
        writer.write("?: ");

        Set<AbstractType> parameter = functionsParameters.get(paramIndex);
        writeMultiType(parameter, writer);
        first = false;
      }
      writer.write("): ");
      writeMultiType(resultTypes, writer);
      writer.write("{\n");

      List<Map.Entry<FunctionType, RestMethod>> sortedFunctionDefs =
          functionTypesToPrivateRestMethodMap
              .entrySet()
              .stream()
              .sorted((a, b) -> {
                int aSize = a.getKey().getParameters().size();
                int bSize = b.getKey().getParameters().size();
                if (aSize == bSize) {
                  return 0;
                }
                return aSize > bSize ? -1 : 1;
              })
              .collect(toList());

      List<Integer> rhl = sortedFunctionDefs.stream().mapToInt(e -> e.getKey().getParameters().size()).boxed().collect(toList());
      Collections.reverse(rhl);
      Iterator<Integer> rh = rhl.iterator();
      List<Integer> distinct = rhl.stream().distinct().collect(toList());

      // If the number of arguments per overloaded method are distinct
      if (distinct.size() == rhl.size()) {

        for (Iterator<Map.Entry<FunctionType, RestMethod>> functionDefIterator = sortedFunctionDefs.iterator(); functionDefIterator.hasNext(); ) {
          Map.Entry<FunctionType, RestMethod> functionDef = functionDefIterator.next();
          if (functionDefIterator.hasNext()) {
            writer.write(String.format("    if (arguments.length > %d ) {\n", rh.next()));
            writeFunctionCall(functionDef.getKey(), functionDef.getValue(), parameterNames, writer);
            writer.write("    }\n");
          } else {
            writeFunctionCall(functionDef.getKey(), functionDef.getValue(), parameterNames, writer);
          }
        }
      } else {
        int diffIndex = findDifferentiatingIndex();

        if (diffIndex >= 0) {
          for (FunctionType functionType: functionTypes) {
            AbstractType param = functionType.getParameters().values().stream().collect(toList()).get(diffIndex);
            String token = "object";
            if (param instanceof AbstractPrimitiveType) {
              token = ((AbstractPrimitiveType) param).getToken();
            }
            writer.write("    if (typeof " + parameterNames.get(diffIndex) + " === '" + token + "') {\n");
            writeFunctionCall(functionType, functionTypesToPrivateRestMethodMap.get(functionType), parameterNames, writer);
            writer.write("    }\n");
          }
        }
        writer.write("    throw new Error('Ambiguous method call?');\n");
      }

      writer.write("  }\n\n");
  }

  private int findDifferentiatingIndex() {
    int count = 0;
    for (Set<AbstractType> types: functionsParameters) {
      if (types.size() > 1) {
        return count;
      }
      count++;
    }
    return -1;
  }

  private void writeFunctionCall(FunctionType functionType, RestMethod restMethod, List<String> finalParamNames, Writer writer) throws IOException {
    writer.write("      return this.");
    writer.write(restMethod.getName());
    writer.write("(");

    List<Map.Entry<String, AbstractType>> privParams = functionType.getParameters().entrySet().stream().collect(toList());
    for (int i = 0; i < privParams.size(); i++ ) {
      if (i > 0) {
        writer.write(", ");
      }
      writer.write(finalParamNames.get(i));
      writer.write(" as ");
      privParams.get(i).getValue().write(writer);
    }

    writer.write(");\n");
  }

  private void writeMultiType(Set<AbstractType> types, Writer writer) throws IOException  {
    boolean first = true;
    for (AbstractType type : types) {
      if (!first) {
        writer.write("|");
      }
      type.write(writer);
      first = false;
    }
  }

  private void writeNonLambda(String visibility, String name, FunctionType functionType, Writer writer) throws IOException {
    writer.write("  " + visibility + " " + name);
    functionType.writeNonLambda(writer);
    writer.write(";\n");
  }

  @Override
  public void write(Writer writer) throws IOException {
    for (FunctionType functionType: functionTypes) {
      writeNonLambda("public", functionTypesToRestMethodMap.get(functionType).getName(), functionType, writer);
    }
    writeOverloadedMethod(writer);
    for (FunctionType functionType: functionTypes) {
      functionTypesToPrivateRestMethodMap.get(functionType).write(functionType, writer, basePath, false);
    }
  }

}
