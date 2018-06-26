package java2typescript.jackson.module;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dasherize {
  public static final Pattern pattern = Pattern.compile("[A-Z\\-]{2,}+|[A-Z\\-]");

  public static final String convert(String text) {
    text = text.replace("_", "-");
    Matcher matcher = pattern.matcher(text);
    StringBuilder sb = new StringBuilder();
    int lastIndex = 0;
    while (matcher.find()) {
      if (lastIndex > 0) {
        sb.append("-");
      }
      if (lastIndex < matcher.start()) {
        sb.append(text.substring(lastIndex, matcher.start()).toLowerCase());
        lastIndex = matcher.start();
      } else if (matcher.end() - matcher.start() == text.length()) {
        sb.append(text.toLowerCase());
        lastIndex = text.length();
      } else {
        sb.append(text.substring(matcher.start(), matcher.end() - 1).toLowerCase());
        lastIndex = matcher.end() - 1;
      }
    }
    if (lastIndex + 1 < text.length()) {
      if (lastIndex > 0) {
        sb.append("-");
      }
      sb.append(text.substring(lastIndex).toLowerCase());
    }
    return sb.toString();
  }
}
