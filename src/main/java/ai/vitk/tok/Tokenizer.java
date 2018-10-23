package ai.vitk.tok;

import ai.vitk.type.Token;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by phuonglh on 2/17/17.
 * <p>
 *   A Vietnamese tokenizer.
 * </p>
 */
public class Tokenizer implements Serializable {
  
  RegExpTokenizer regExpTokenizer = new RegExpTokenizer(); 
  
  public Tokenizer() {
    this(new DefaultDictionary());
  }

  public Tokenizer(Dictionary dictionary) {
    regExpTokenizer.setDictionary(dictionary);
  }

  /**
   * Iterates all possible word segmentations
   * @param text a raw text
   * @return a list of segmentations.
   */
  public synchronized List<List<Token>> iterate(String text) {
    List<List<Token>> candidates = regExpTokenizer.iterate(text);
    return candidates.stream().map(candidate -> split(merge(candidate))).collect(Collectors.toList());
  }
  
  /**
   * Segments a text into tokens.
   * @param text a raw text.
   * @return a list of tokens.
   */
  public synchronized List<Token> tokenize(String text) {
    List<Token> tokens = regExpTokenizer.tokenize(text);
    return split(merge(tokens));
  }

  /**
   * Splits one token into two by using the prefix, for example ["Ông Phương"] => ["Ông", "Phương"];
   * however, pay attention to case like ["Xã hội"], where the second syllable starts with a lowercase.
   * @param tokens
   * @return a list of tokens
   */
  private List<Token> split(List<Token> tokens) {
    List<Token> result = new LinkedList<>();
    for (Token token: tokens) {
      String word = token.getWord();
      int j = word.indexOf(' ');
      if (j > 0) {
        String prefix = word.substring(0, j);
        String rest = word.substring(j+1);
        if (Prefixes.all.contains(prefix.toLowerCase()) && rest.length() > 0 && Character.isUpperCase(rest.charAt(0))) {
          Token t1 = new Token(token.getId(), prefix).setLemma("WORD");
          Token t2 = new Token(token.getId(), word.substring(j+1)).setLemma(token.getLemma());
          result.add(t1);
          result.add(t2);
        } else {
          result.add(token);
        }
      } else {
        result.add(token);
      }
    }
    for (int j = 0; j < result.size(); j++) {
      result.get(j).setId(Integer.toString(j));
    }
    return result;
  }

  private boolean canMerge(Token token) {
    return token.getLemma().equals("NAME") || token.getLemma().equals("CAPITAL");
  }
  
  /**
   * Merge two consecutive tokens, for example ["Thủ", "tướng"] => ["Thủ tướng"].  
   * @param tokens
   * @return a list of tokens
   */
  private List<Token> merge(List<Token> tokens) {
    List<Token> result = tokens;
    int n = result.size();
    for (int i = n-2; i >= 0; i--)
      if (canMerge(result.get(i))) {
        List<Token> left = result.subList(0, i);
        List<Token> right = merge(result, i);
        result = left;
        result.addAll(right);
      }
    for (int j = 0; j < result.size(); j++) {
      result.get(j).setId(Integer.toString(j));
    }
    return result;
  }

  private synchronized List<Token> merge(List<Token> tokens, int i) {
    if (i > tokens.size() - 1)
      return new LinkedList<>();
    boolean yes = false;
    Token first = tokens.get(i);
    String w = first.getWord();
    if (canMerge(first)) {
      if (i < tokens.size() - 1) {
        Token second = tokens.get(i + 1);
        if (second.getLemma().equals("WORD")) {
          String s = w;
          int j = w.lastIndexOf(' ');
          if (j > 0) {
            s = w.substring(j + 1);
          }
          s = s.toLowerCase() + ' ' + second.getWord();
          if (Lexicon.INSTANCE.hasWord(s)) {
            yes = true;
            w = w + ' ' + second.getWord();
          }
        }
      }
    }
    List<Token> result = new LinkedList<>();
    result.add(new Token("" + i, w).setLemma(first.getLemma()));
    if (yes) {
      result.addAll(tokens.subList(i+2, tokens.size()));
    } else {
      result.addAll(tokens.subList(i+1, tokens.size()));
    }
    return result;
  }

  /**
   * Runs the tokenizer on a text of a language (either English or Vietnamese).
   * @param isEnglish true if English, false if Vietnamese
   * @param text a text.
   * @return a list of tokens.
   */
  public synchronized List<Token> run(boolean isEnglish, String text) {
    if (isEnglish)
      return RegExpTokenizer.split(text);
    return tokenize(text);
  }
  
  public static void main(String[] args) throws IOException {
    Tokenizer tokenizer = new Tokenizer();
    if (args.length == 0) {
      Scanner scanner = new Scanner(System.in);
      String text;
      System.out.println("Enter a text. Empty string to exit. ");
      do {
        text = scanner.nextLine();
        if (text.trim().length() > 0) {
          List<List<Token>> segmentations = tokenizer.iterate(text);
          System.out.println("Number of possible segmentations = " + segmentations.size());
          for (int i = 0; i < segmentations.size(); i++) {
            List<Token> a = segmentations.get(i);
            System.out.print((i+1) + ". ");
            a.forEach(token -> System.out.print(token));
            System.out.println();
          }
        }
      } while (text.trim().length() > 0);
    } else {
      String inputFile = args[0];
      List<String> inputs = Files.readAllLines(Paths.get(inputFile), StandardCharsets.UTF_8);
      List<String> outputs = new LinkedList<>();
      for (String input: inputs) {
        List<Token> tokens = tokenizer.run(false, input);
        StringBuilder sb = new StringBuilder();
        tokens.forEach(token -> sb.append(token.getWord().replaceAll("\\s+", "_") + '/' + token.getLemma() + ' '));
        outputs.add(sb.toString().trim());
      }
      if (args.length > 1) {
        String outputFile = args[1];
        Files.write(Paths.get(outputFile), outputs, StandardCharsets.UTF_8);
      } else {
        outputs.forEach(o -> System.out.println(o));
      }
    }
  }
}
