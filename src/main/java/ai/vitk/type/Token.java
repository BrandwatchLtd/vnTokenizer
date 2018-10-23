package ai.vitk.type;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by phuonglh on 2/16/17.
 * <p>
 *   A token.
 * </p>
 */
public class Token {
  private String id;
  private String word;
  private Map<Annotation, String> annotation;

  public Token(String id, String word) {
    this.id = id;
    this.word = word;
    this.annotation = new TreeMap<>();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWord() {
    return word;
  }

  public Map<Annotation, String> getAnnotation() {
    return annotation;
  }

  public Token setLemma(String type) {
    annotation.put(Annotation.LEM, type);
    return this;
  }

  public String getLemma() {
    return annotation.get(Annotation.LEM);
  }

  public Token setPartOfSpeech(String type) {
    annotation.put(Annotation.POS, type);
    return this;
  }

  public String getPartOfSpeech() {
    return annotation.get(Annotation.POS);
  }

  public Token setNamedEntityType(String type) {
    annotation.put(Annotation.NET, type);
    return this;
  }

  public String getNamedEntity() {
    return annotation.get(Annotation.NET);
  }

  public Token setLabel(String type) {
    annotation.put(Annotation.LAB, type);
    return this;
  }

  public String getLabel() {
    return annotation.get(Annotation.LAB);
  }

  public Token setGovernor(String head) {
    annotation.put(Annotation.GOV, head);
    return this;
  }

  public String getGovernor() {
    return annotation.get(Annotation.GOV);
  }

  public Token setDependency(String dependency) {
    annotation.put(Annotation.DEP, dependency);
    return this;
  }

  public String getDependency() {
    return annotation.get(Annotation.DEP);
  }

  public Token setSuperTag(String superTag) {
    annotation.put(Annotation.SUP, superTag);
    return this;
  }

  public String getSuperTag() {
    return annotation.get(Annotation.SUP);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(id);
    sb.append(';');
    sb.append(word.replaceAll("\\s+", "_"));
    if (!annotation.isEmpty()) {
      sb.append(';');
      sb.append(annotation);
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * Makes a copy of this token.
   * @return a copied version of this token.
   */
  public Token copy() {
    Map<Annotation, String> map = new HashMap<>();
    for (Annotation a : annotation.keySet()) {
      map.put(a, annotation.get(a));
    }
    Token t = new Token(id, word);
    t.annotation = map;
    return t;
  }
}
