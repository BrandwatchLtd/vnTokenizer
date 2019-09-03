package ai.vitk.tok;


import ai.vitk.type.ImmutablePair;
import ai.vitk.type.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by phuonglh on 2/17/17.
 */

public class PhraseGraph implements Serializable {
  static boolean verbose = false;
  private static Logger logger = Logger.getLogger(PhraseGraph.class.getName());
  private static TextNormalizer NORMALIZER = new TextNormalizer();
  
  private final Dictionary dictionary;
  
  public PhraseGraph() {
    this(DefaultDictionary.Data.INSTANCE);
  }
  
  public PhraseGraph(final Dictionary dictionary) {
    this.dictionary = dictionary;
  }
  
  public static final class GraphPaths {
      
    private final Syllable[] syllables;
    private final List<LinkedList<Integer>> paths;
      
    protected GraphPaths(final Syllable[] syllables, final List<LinkedList<Integer>> paths) {
      this.syllables = syllables;
      this.paths = paths;
    }

    public final boolean isEmpty() {
      return this.paths.isEmpty();
    }
    
    public final int size() {
      return this.paths.size();
    }

    /**
     * Gets a list of words specified by a given path.
     * @param pathIdx
     * @return a list of words.
     */
    public final List<String> words(final int pathIdx) {
      final List<Integer> path = this.paths.get(pathIdx);
      final int m = path.size();
      if (m <= 1)
        return null;
      final Integer[] a = path.toArray(new Integer[m]);
      final StringBuilder[] tok = new StringBuilder[m-1];
      int i;
      for (int j = 0; j < m-1; j++) {
        // get the token from a[j] to a[j+1] (exclusive)
        tok[j] = new StringBuilder();
        i = a[j];
        tok[j].append(syllables[i].original);
        for (int k = a[j]+1; k < a[j+1]; k++) {
          tok[j].append(' ');
          tok[j].append(syllables[k].original);
        }
      }
      final List<String> result = new ArrayList<>(tok.length);
      for (final StringBuilder sb : tok) {
        result.add(sb.toString());
      }
      return result;
    }

    /**
     * Gets a sub-sequence of syllables from a segment marking 
     * the beginning and the end positions.
     * @param segment
     * @return
     */
    String words(final Pair<Integer, Integer> segment) {
      final StringBuilder sb = new StringBuilder();
      for (int i = segment.getLeft(); i < segment.getRight(); i++) {
        sb.append(syllables[i]);
        sb.append(' ');
      }
      return sb.toString().trim();
    }

    /**
     * Finds all overlap groups of syllables of this graph. A syllable group is overlap if
     * it defines multiple shortest paths, e.g. 4 nodes and two paths: [0, 1, 3] or [0, 2, 3]. 
     * @return a list of syllable groups, each is a tuple of begin and end position.
     */
    List<Pair<Integer, Integer>> overlaps(final Map<Integer, LinkedList<Integer>> edges) {
      final List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
      if (syllables.length >= 4) {
        int i = syllables.length-1;
        while (i >= 3) {
          if (edges.get(i).contains(i-1) && edges.get(i).contains(i-2)
              && edges.get(i-1).contains(i-3) && edges.get(i-2).contains(i-3)) {
            result.add(new ImmutablePair<>(i-3, i));
            i = i - 4;
          } else {
            i = i - 1;
          }
        }
      }
      return result;
    }

  }
  
  /**
   * For each vertex v, we store a list of vertices u where (u, v) is an edge
   * of the graph. This is used to recursively search for all paths on the graph.  
   */
  private GraphPaths makeShortestPaths(final String phrase) {
      final Map<Integer, LinkedList<Integer>> edges = new HashMap<>();
      final Syllable[] syllables = Arrays.stream(phrase.split("\\s+"))
        .map(Syllable::new)
        .toArray(Syllable[]::new);
      if (verbose && syllables.length > 128) {
        logger.log(Level.WARNING, "Phrase too long (>= 128 syllables), tokenization may be slow...");
        logger.log(Level.WARNING, phrase);
      }
      for (int j = 0; j <= syllables.length; j++) {
        edges.put(j, new LinkedList<>());
      }
      for (int i = 0; i < syllables.length; i++) {
        String token = syllables[i].normalised;
        int j = i;
        while (j < syllables.length) {
          if (dictionary.hasWord(token)) {
            edges.get(j+1).add(i);
          }
          j++;
          if (j < syllables.length) {
            token = token + ' ' + syllables[j].normalised;
          }
        }
      }
      // make sure that the graph is connected by adding adjacent 
      // edges if necessary
      for (int i = syllables.length; i > 0; i--) {
        if (edges.get(i).size() == 0) { // i cannot reach by any previous node
          edges.get(i).add(i-1);
        }
      }
      return new GraphPaths(syllables, new Dijkstra(edges).shortestPaths());
  }

  /**
   * Finds all shortest paths from the first node to the last node
   * of this graph. 
   * @return a list of paths, each path is a linked list of vertices.
   */
  public GraphPaths shortestPaths(final String text) {
    final GraphPaths syllablePaths = this.makeShortestPaths(text);
    if (verbose) {
      if (syllablePaths.paths.size() > 16) {
        final StringBuilder phrase = new StringBuilder();
        for (final Syllable syllable : syllablePaths.syllables) {
          phrase.append(syllable.original);
          phrase.append(' ');
        }
        logger.log(
            Level.WARNING, 
            String.format(
                "This phrase is too ambiguous, giving %d shortest paths!\n\t%s\n",
                syllablePaths.paths.size(), phrase.toString().trim()
            )
        );
      }
    }
    return syllablePaths;
  }

  static final class TextNormalizer implements Serializable {
    private final Map<String, String> vowels = new HashMap<String, String>();
    private final Pattern pattern = Pattern.compile("[hklmst][yỳýỷỹỵ]");
    private final Map<Character, Character> ymap = new HashMap<Character, Character>();

    public TextNormalizer() {
      vowels.put("òa", "oà");
      vowels.put("óa", "oá");
      vowels.put("ỏa", "oả");
      vowels.put("õa", "oã");
      vowels.put("ọa", "oạ");
      vowels.put("òe", "oè");
      vowels.put("óe", "oé");
      vowels.put("ỏe", "oẻ");
      vowels.put("õe", "oẽ");
      vowels.put("ọe", "oẹ");
      vowels.put("ùy", "uỳ");
      vowels.put("úy", "uý");
      vowels.put("ủy", "uỷ");
      vowels.put("ũy", "uỹ");
      vowels.put("ụy", "uỵ");
      // initialize the y map
      ymap.put('y', 'y');
      ymap.put('ỳ', 'ì');
      ymap.put('ý', 'í');
      ymap.put('ỷ', 'ỉ');
      ymap.put('ỹ', 'ĩ');
      ymap.put('ỵ', 'ị');
    }

    public String normalize(String phrase) {
      // normalize all the vowels of the phrase
      for (final String u : vowels.keySet()) {
        final String v = vowels.get(u);
        phrase = phrase.replace(u, v);
      }
      // normalize the i/y by converting 'y' to 'i'  
      // if 'y' goes after consonants "h, k, l, m, s, t".
      StringBuilder sb = new StringBuilder(phrase);
      final Matcher matcher = pattern.matcher(phrase);
      while (matcher.find()) {
        final int idx = matcher.start() + 1;
        sb = sb.replace(idx, matcher.end(), String.valueOf(ymap.get(sb.charAt(idx))));
      }
      return sb.toString();
    }

  }

  private static class Syllable {
    private final String original;
    private final String normalised;

    Syllable(final String original) {
      this.original = original;
      this.normalised = NORMALIZER.normalize(original);
    }
  }
  
}
