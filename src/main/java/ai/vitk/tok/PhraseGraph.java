package ai.vitk.tok;


import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ai.vitk.type.*;

/**
 * Created by phuonglh on 2/17/17.
 */

public class PhraseGraph implements Serializable {
  private boolean verbose = false;
  private String[] syllables;
  private int n;
  private Dictionary dictionary;
  
  public PhraseGraph() {
    this(new DefaultDictionary());
  }
  
  public PhraseGraph(Dictionary dictionary) {
    this.dictionary = dictionary;
  }
  
  /**
   * For each vertex v, we store a list of vertices u where (u, v) is an edge
   * of the graph. This is used to recursively search for all paths on the graph.  
   */
  private Map<Integer, LinkedList<Integer>> edges = Collections.synchronizedMap(new HashMap<>());
  private TextNormalizer textNormalizer = new TextNormalizer();
  
  public synchronized void makeGraph(String phrase) {
    edges.clear();
    syllables = textNormalizer.normalize(phrase).split("\\s+");
    n = syllables.length;
    if (n > 128) {
      System.out.println("WARNING: Phrase too long (>= 128 syllables), tokenization may be slow...");
      System.out.println(phrase);
    }
    for (int j = 0; j <= n; j++) {
      edges.put(j, new LinkedList<>());
    }
    for (int i = 0; i < n; i++) {
      String token = syllables[i];
      int j = i;
      while (j < n) {
        if (dictionary.hasWord(token)) {
          edges.get(j+1).add(i);
        }
        j++;
        if (j < n) {
          token = token + ' ' + syllables[j];
        }
      }
    }
    // make sure that the graph is connected by adding adjacent 
    // edges if necessary
    for (int i = n; i > 0; i--) {
      if (edges.get(i).size() == 0) { // i cannot reach by any previous node
        edges.get(i).add(i-1);
      }
    }
  }

  /**
   * Finds all shortest paths from the first node to the last node
   * of this graph. 
   * @return a list of paths, each path is a linked list of vertices.
   */
  public synchronized List<LinkedList<Integer>> shortestPaths() {
    Dijkstra dijkstra = new Dijkstra(edges);
    List<LinkedList<Integer>> allPaths = dijkstra.shortestPaths();
    if (verbose) {
      if (allPaths.size() > 16) {
        StringBuilder phrase = new StringBuilder();
        for (String syllable : syllables) {
          phrase.append(syllable);
          phrase.append(' ');
        }
        System.out.printf("This phrase is too ambiguous, giving %d shortest paths!\n\t%s\n",
            allPaths.size(), phrase.toString().trim());
      }
    }
    return allPaths;
  }

  /**
   * Gets a list of words specified by a given path.
   * @param path
   * @return a list of words.
   */
  public synchronized List<String> words(LinkedList<Integer> path) {
    int m = path.size();
    if (m <= 1)
      return null;
    Integer[] a = path.toArray(new Integer[m]);
    StringBuilder[] tok = new StringBuilder[m-1];
    int i;
    for (int j = 0; j < m-1; j++) {
      // get the token from a[j] to a[j+1] (exclusive)
      tok[j] = new StringBuilder();
      i = a[j];
      tok[j].append(syllables[i]);
      for (int k = a[j]+1; k < a[j+1]; k++) {
        tok[j].append(' ');
        tok[j].append(syllables[k]);
      }
    }
    List<String> result = new LinkedList<String>();
    for (StringBuilder sb : tok) {
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
  public synchronized String words(Pair<Integer, Integer> segment) {
    StringBuilder sb = new StringBuilder();
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
  public synchronized List<Pair<Integer, Integer>> overlaps() {
    List<Pair<Integer, Integer>> result = new ArrayList<Pair<Integer, Integer>>();
    if (n >= 4) {
      int i = n-1;
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

  @Override
  public String toString() {
    return edges.toString();
  }

  class TextNormalizer implements Serializable {
    private Map<String, String> vowels = new HashMap<String, String>();
    private Pattern pattern = Pattern.compile("[hklmst][yỳýỷỹỵ]");
    private Map<Character, Character> ymap = new HashMap<Character, Character>();

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

    public synchronized String normalize(String phrase) {
      // normalize all the vowels of the phrase
      for (String u : vowels.keySet()) {
        String v = vowels.get(u);
        phrase = phrase.replace(u, v);
      }
      // normalize the i/y by converting 'y' to 'i'  
      // if 'y' goes after consonants "h, k, l, m, s, t".
      StringBuilder sb = new StringBuilder(phrase);
      Matcher matcher = pattern.matcher(phrase);
      while (matcher.find()) {
        int idx = matcher.start() + 1;
        sb = sb.replace(idx, matcher.end(), String.valueOf(ymap.get(sb.charAt(idx))));
      }
      return sb.toString();
    }
  }
  
}
