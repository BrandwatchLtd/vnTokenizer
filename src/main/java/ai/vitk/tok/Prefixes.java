package ai.vitk.tok;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * phuonglh, 4/9/18, 5:29 PM
 * <p>
 */
public class Prefixes {
  static Set<String> all = read();
  
  private static Set<String> read() {
    InputStream inputStream = Prefixes.class.getResourceAsStream("/tok/prefixes.txt");
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
      return reader.lines().collect(Collectors.toSet());
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new HashSet<>();
  }
}
