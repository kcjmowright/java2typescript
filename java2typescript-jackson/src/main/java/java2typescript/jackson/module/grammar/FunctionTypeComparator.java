package java2typescript.jackson.module.grammar;

import java.io.StringWriter;
import java.util.Comparator;

public class FunctionTypeComparator implements Comparator<FunctionType> {
  @Override
  public int compare(FunctionType a, FunctionType b) {
      int aSize = a.getParameters().size();
      int bSize = b.getParameters().size();

      if (aSize == bSize) {
        StringWriter writer1 = new StringWriter();
        StringWriter writer2 = new StringWriter();
        try {
          a.writeNonLambda(writer1);
          b.writeNonLambda(writer2);
          return writer1.toString().compareTo(writer2.toString());
        }
        catch(Exception e) {
          return 0;
        }
      }
      return aSize > bSize ? -1 : 1;
  }
}
