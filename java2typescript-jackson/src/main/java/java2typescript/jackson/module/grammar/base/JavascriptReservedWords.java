package java2typescript.jackson.module.grammar.base;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavascriptReservedWords {

  /* formatter: off */
  public static final Set<String> RESERVED_WORDS = Set.of(
    "break",
    "case",
    "catch",
    "class",
    "const",
    "continue",
    "debugger",
    "default",
    "delete",
    "do",
    "else",
    "export",
    "extends",
    "finally",
    "for",
    "function",
    "if",
    "import",
    "in",
    "instanceof",
    "new",
    "return",
    "super",
    "switch",
    "this",
    "throw",
    "try",
    "typeof",
    "var",
//    "void",
    "while",
    "with",
    "yield"
  );
  /* formatter: on */

  public static String sanitize(String token) {
    if (token != null && RESERVED_WORDS.contains(token)) {
      return "_" + token;
    }
    return token;
  }

  public static String[] sanitizeAll(String[] tokens) {
    return tokens != null ?
        Arrays.stream(tokens).map(t -> sanitize(t)).collect(Collectors.toList()).toArray(tokens) :
        null;
  }

  public static String sanitizeJavaName(String javaName) {
    return javaName == null ? null : String.join(".", sanitizeAll(javaName.split("\\.")));
  }

  public static String sanitizePath(String path) {
    if (path == null) {
      return null;
    }
    Matcher matcher = Pattern.compile("\\{([^}]*)}").matcher(path);
    boolean result = matcher.find();
    if (result) {
      StringBuilder sb = new StringBuilder();
      do {
        String replacement = sanitize(matcher.group(1));
        matcher.appendReplacement(sb, String.format("{%s}", replacement));
        result = matcher.find();
      } while (result);
      matcher.appendTail(sb);
      return sb.toString();
    }
    return path;
  }
}
