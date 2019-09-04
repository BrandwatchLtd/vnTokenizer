package ai.vitk.tok;

import java.io.Serializable;

/**
 * phuonglh, 4/2/18, 12:34 PM
 * <p>
 */
public class DefaultDictionary implements Dictionary, Serializable {
  
  public static final class Data { // prevents loading the default dictionary multiple times
      public static final Dictionary INSTANCE = new DefaultDictionary();
  }
  
  protected DefaultDictionary() {
  }
  
  private final Lexicon lexicon = new Lexicon().load(Lexicon.class.getResourceAsStream("/tok/lexicon.xml"))
      .additionalLexicon("/tok/provinces.txt").additionalLexicon("/tok/districts.txt")
      .additionalLexicon("/tok/extra.txt");
  
  @Override
  public boolean hasWord(String word) {
    return lexicon.hasWord(word);
  }
  
}
