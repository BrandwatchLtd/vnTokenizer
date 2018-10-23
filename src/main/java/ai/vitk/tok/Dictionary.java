package ai.vitk.tok;

/**
 * A dictionary used in the word segmentation module.
 */
public interface Dictionary {
  boolean hasWord(String word);
}
