package ai.vitk.type;

/**
 * Created by phuonglh on 3/6/17.
 * <p>
 *   Each pattern has a name, a regular expression pattern, and a priority.
 *   The matching will be done by using the priority order of a pattern instead 
 *   of the longest match.
 * </p>
 */
public class Pattern implements Comparable<Pattern> {
  private String name;
  private java.util.regex.Pattern pattern;
  private int priority;

  public Pattern(String name, java.util.regex.Pattern pattern, int priority) {
    this.name = name;
    this.pattern = pattern;
    this.priority = priority;
  }
  
  public String getName() {
    return name;
  }

  public java.util.regex.Pattern getPattern() {
    return pattern;
  }

  public int getPriority() {
    return priority;
  }

  @Override
  public int compareTo(Pattern o) {
    if (this.priority > o.priority)
      return 1;
    if (this.priority < o.priority)
      return -1;
    return 0;
  }

  @Override
  public int hashCode() {
    return 31 * name.hashCode() + priority;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null)
      return false;
    if (obj == this)
      return true;
    if (!(obj instanceof Pattern))
      return false;
    Pattern p = (Pattern) obj;
    return name.equals(p.getName()) && priority == p.getPriority();
  }

  @Override
  public String toString() {
    return "(" + name + ", " + priority + ")";
  }
}
