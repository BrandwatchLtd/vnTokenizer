package ai.vitk.type;

public class ImmutablePair<E, F> implements Pair<E, F> {
  private E left;
  private F right;
  
  public ImmutablePair(E left, F right) {
    this.left = left;
    this.right = right;
  }
  
  @Override
  public E getLeft() {
    return left;
  }

  @Override
  public F getRight() {
    return right;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    sb.append(left.toString());
    sb.append(',');
    sb.append(right.toString());
    sb.append(')');
    return sb.toString();
  }
}
