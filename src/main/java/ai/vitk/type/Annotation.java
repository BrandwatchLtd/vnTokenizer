package ai.vitk.type;

/**
 * Created by phuonglh on 2/18/17.
 */
public enum Annotation {
  LEM("lem"),   // lemma
  NET("net"),   // named entity
  POS("pos"),   // part-of-speech tag
  LAB("lab"),   // label
  GOV("head"),  // head
  DEP("dep"),   // dependency
  SUP("sup");   // super-tag (elementary tree)

  private final String name;

  Annotation(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
