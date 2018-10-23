package ai.vitk.tok;

import java.io.Serializable;
import java.util.List;

/**
 * phuonglh, 4/2/18, 12:34 PM
 * <p>
 */
public class DefaultDictionary implements Dictionary, Serializable {
  private Lexicon lexicon = new Lexicon().load(Lexicon.class.getResourceAsStream("/tok/lexicon.xml"))
      .additionalLexicon("/tok/provinces.txt").additionalLexicon("/tok/districts.txt")
      .additionalLexicon("/tok/extra.txt");
  
  @Override
  public boolean hasWord(String word) {
    return lexicon.hasWord(word);
  }
  
  public void addWords(List<String> words) {
    for (String word: words)
      lexicon.addWord(word);
  }
}
