package java2typescript.jackson.module;

import java2typescript.jackson.module.grammar.AngularObservableType;
import java2typescript.jackson.module.grammar.ArrayType;
import java2typescript.jackson.module.grammar.MapType;
import java2typescript.jackson.module.grammar.base.AbstractNamedType;
import java2typescript.jackson.module.grammar.base.AbstractType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class PathResolver {

  private static PathResolver pathResolver;

  private PathResolver() {}

  public static PathResolver getResolver() {
    if (pathResolver == null) {
      pathResolver = new PathResolver();
    }
    return pathResolver;
  }

  public AbstractNamedType resolveNamedType(Map<AbstractNamedType, String> imports, AbstractType abstractType) {
    if(abstractType instanceof AngularObservableType) {
      AngularObservableType observable = (AngularObservableType) abstractType;
      imports.put(observable, "rxjs/Observable");
      return resolveNamedType(imports, observable.getType());
    } else if (abstractType instanceof AbstractNamedType) {
      return (AbstractNamedType) abstractType;
    } else if (abstractType instanceof ArrayType) {
      return resolveNamedType(imports, ((ArrayType) abstractType).getItemType());
    } else if (abstractType instanceof MapType) {
      return resolveNamedType(imports, ((MapType) abstractType).getValueType());
    }
    return null;
  }

  public void setupImport(Map<AbstractNamedType, String> imports, List<String> packagePathList1, AbstractNamedType namedType) {
    if (namedType == null) {
      return;
    }
    List<String> thePackagePathList = new ArrayList<>(packagePathList1);
    List<String> packagePathList2 = new ArrayList<>(Arrays.asList(namedType.getPackagePath()));
    imports.put(namedType, resolveImportsPath(thePackagePathList, packagePathList2) +
        "/" + namedType.getFileName().replaceAll("\\.ts$", ""));
  }

  /**
   *
   * @param a base
   * @param b other
   * @return relative path of other to base.
   */
  public String resolveImportsPath(String[] a, String[] b) {
    return resolveImportsPath(new ArrayList<>(Arrays.asList(a)), new ArrayList<>(Arrays.asList(b)));
  }

  /**
   *
   * @param a base
   * @param b other
   * @return relative path of other to base.
   */
  public String resolveImportsPath(List<String> a, List<String> b) {
    if (a.equals(b)) {
      return ".";
    }
    if (a.size() > b.size()) {
      while (a.size() != b.size()) {
        b.add("");
      }
    } else if (a.size() < b.size()) {
      while (a.size() != b.size()) {
        a.add("");
      }
    }
    boolean look = false;
    Stack<String> stack = new Stack<>();
    Stack<String> upStack = new Stack<>();

    for (int i = a.size(); --i >= 0;) {
      if (look &&
          a.get(i).equalsIgnoreCase(b.get(i)) &&
          String.join(".", a.subList(0, i).toArray(new String[]{})).equalsIgnoreCase(
              String.join(".", b.subList(0, i).toArray(new String[]{})))
          ) {
        break;
      } else if (!a.get(i).equalsIgnoreCase(b.get(i))) {
        look = true;
      }
      if (!"".equalsIgnoreCase(a.get(i))) {
        upStack.add("..");
      }
      if (!"".equalsIgnoreCase(b.get(i))) {
        stack.add(b.get(i));
      }
    }
    Collections.reverse(stack); // <- Flip it and reverse it.

    StringBuilder sb = new StringBuilder(String.join("/", upStack.toArray(new String[]{})));
    if (stack.size() > 0) {
      if (sb.length() == 0) {
        sb.append(".");
      }
      sb.append("/").append(String.join("/", stack.toArray(new String[]{})));
    }
    return sb.toString();
  }
}
