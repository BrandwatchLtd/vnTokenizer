package ai.vitk.tok;

import ai.vitk.util.TokenShape;
import ai.vitk.type.Token;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by phuonglh on 11/16/16.
 * <p>
 * Implementation of a simple regexp tokenizer for Vietnamese.
 * </p>
 */
public class RegExpTokenizer implements Serializable {
  static boolean verbose = false;
  static Logger logger = Logger.getLogger(RegExpTokenizer.class.getName());
  private PhraseGraph graph = new PhraseGraph();
  static List<ai.vitk.type.Pattern> patterns = new LinkedList<>();
  static {
    patterns.addAll(TokenShape.COMMON_PATTERNS);
    Collections.sort(patterns, Comparator.reverseOrder());
  }

  static List<ai.vitk.type.Pattern> extendedPatterns = new LinkedList<>();
  static {
    extendedPatterns.addAll(TokenShape.EXTENDED_PATTERNS);
    Collections.sort(extendedPatterns, Comparator.reverseOrder());
  }

  /**
   * Sets the dictionary used in this tokenizer.
   * @param dictionary
   */
  public void setDictionary(Dictionary dictionary) {
    graph = new PhraseGraph(dictionary);
  }
  
  /**
   * Segments a Vietnamese text into tokens.
   * @param text a text (plain sentence)
   * @return a list of tokens
   */
  public synchronized List<Token> tokenize(String text) {
    text = text.trim();
    if (text.isEmpty())
      return new LinkedList<>();
    int start = -1;
    String token = "";
    String regexp = "";
    for (ai.vitk.type.Pattern p : patterns) {
      Pattern pattern = p.getPattern();
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        token = matcher.group();
        start = matcher.start();
        regexp = p.getName();
        break;
      }
    }
    if (start >= 0) {
      List<Token> tokens = new LinkedList<>();
      List<Token> left = tokenize(text.substring(0, start));
      List<Token> right = tokenize(text.substring(start + token.length()));
      tokens.addAll(left);
      if (!regexp.equals("PHRASE")) {
        tokens.add(new Token("0", token.trim()).setLemma(regexp));
      } else {
        graph.makeGraph(token.trim());
        List<LinkedList<Integer>> paths = graph.shortestPaths();
        if (!paths.isEmpty()) {
          LinkedList<Integer> selectedPath = paths.get(paths.size() - 1);
          List<String> words = graph.words(selectedPath);
          for (String word : words) {
            tokens.add(new Token("0", word).setLemma("WORD"));
          }
        } else {
          logger.log(Level.WARNING, "Cannot tokenize the following phrase: [" + token.trim() + "]");
        }
      }
      tokens.addAll(right);
      return tokens;
    } else {
      if (verbose)
        logger.log(Level.WARNING, "Cannot be matched by any regular expression! " + text);
      return new LinkedList<>();
    }
  }

  /**
   * Segments an English text into tokens, simply using space as delimiter for PHRASE types.
   * @param text a text (plain sentence)
   * @return a list of tokens
   */
  public synchronized static List<Token> split(String text) {
    text = text.trim();
    if (text.isEmpty())
      return new LinkedList<>();
    int start = -1;
    String token = "";
    String regexp = "";
    for (ai.vitk.type.Pattern p : extendedPatterns) {
      Pattern pattern = p.getPattern();
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        token = matcher.group();
        start = matcher.start();
        regexp = p.getName();
        break;
      }
    }
    if (start >= 0) {
      List<Token> tokens = new LinkedList<>();
      List<Token> left = split(text.substring(0, start));
      List<Token> right = split(text.substring(start + token.length()));
      tokens.addAll(left);
      if (!regexp.equals("PHRASE")) {
        tokens.add(new Token("0", token.trim()).setLemma(regexp));
      } else {
        String[] ws = token.trim().split("\\s+");
        for (String w: ws)
          tokens.add(new Token("0", w).setLemma("WORD"));
      }
      tokens.addAll(right);
      return tokens;
    } else {
      if (verbose)
        logger.log(Level.WARNING, "Cannot be matched by any regular expression! " + text);
      return new LinkedList<>();
    }
  }

  /**
   * Segments a Vietnamese text into tokens, returns all plausible segmentations.
   * @param text a text (plain sentence)
   * @return a list of segmentations, each is a list of tokens
   */
  public synchronized List<List<Token>> iterate(String text) {
    text = text.trim();
    if (text.isEmpty())
      return new LinkedList<>();
    int start = -1;
    String token = "";
    String regexp = "";
    for (ai.vitk.type.Pattern p : patterns) {
      Pattern pattern = p.getPattern();
      Matcher matcher = pattern.matcher(text);
      if (matcher.find()) {
        token = matcher.group();
        start = matcher.start();
        regexp = p.getName();
        break;
      }
    }
    if (start >= 0) {
      List<List<Token>> left = iterate(text.substring(0, start));
      List<List<Token>> right = iterate(text.substring(start + token.length()));
      List<List<Token>> middle = new LinkedList<>();
      if (!regexp.equals("PHRASE")) {
        Token t = new Token("0", token.trim()).setLemma(regexp);
        List<Token> ts = new LinkedList<>();
        ts.add(t);
        middle.add(ts);
      } else {
        graph.makeGraph(token.trim());
        List<LinkedList<Integer>> paths = graph.shortestPaths();
        if (!paths.isEmpty()) {
          for (int i = 0; i < paths.size(); i++) {
            List<Token> tokens = new LinkedList<>();
            LinkedList<Integer> selectedPath = paths.get(i);
            List<String> words = graph.words(selectedPath);
            for (String word : words) {
              tokens.add(new Token("0", word).setLemma("WORD"));
            }
            middle.add(tokens);
          }
        } else {
          logger.log(Level.WARNING, "Cannot tokenize the following phrase: [" + token.trim() + "]");
        }
      }
      List<List<Token>> result = new LinkedList<>();
      if (left.isEmpty()) {
        if (middle.isEmpty())
          result.addAll(right);
        else {
          // result = middle + right
          if (right.isEmpty())
            result.addAll(middle);
          else {
            for (List<Token> u : middle) {
              for (List<Token> v : right) {
                List<Token> xs = new LinkedList<>(u);
                xs.addAll(v);
                result.add(xs);
              }
            }
          }
        }
      } else {
        if (middle.isEmpty()) {
          if (right.isEmpty()) 
            result.addAll(left);
          else {
            // result = left + right
            for (List<Token> u : left) {
              for (List<Token> v : right) {
                List<Token> xs = new LinkedList<>(u);
                xs.addAll(v);
                result.add(xs);
              }
            }
          }
        } else {
          // uv = left + middle
          List<List<Token>> uv = new LinkedList<>();
          if (middle.isEmpty())
            uv.addAll(left);
          else {
            for (List<Token> u : left) {
              for (List<Token> v : middle) {
                List<Token> xs = new LinkedList<>(u);
                xs.addAll(v);
                uv.add(xs);
              }
            }
          }
          if (right.isEmpty()) 
            result.addAll(uv);
          else {
            // result = uv + right
            for (List<Token> u : uv) {
              for (List<Token> v : right) {
                List<Token> xs = new LinkedList<>(u);
                xs.addAll(v);
                result.add(xs);
              }
            }
          }
        }
      }
      return result;
    } else {
      if (verbose) {
        logger.log(Level.WARNING, "Cannot be matched by any regular expression! " + text);
      }
      return new LinkedList<>();
    }
  }
}
