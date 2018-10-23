package ai.vitk.util;


import java.util.regex.Pattern;

/**
 * Created by phuonglh on 3/8/17.
 * <p>
 *   Regular expressions capturing measurement units.
 * </p>
 */
public class Units {
  static final String NUMBER_S_1 = "\\b(one|two|three|four|five|six|seven|eight|nine)\\b";
  static final String NUMBER_S_2 = "\\b(ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty)\\b";
  static final String NUMBER_S_3 = "\\b(twenty|thirty|forty|fifty|sixty|seventy|eighty|ninety)\\b";
  static final String NUMBER_S_4 = "\\b(hundred|thousand|million|billion)\\b";
  static final String NUMBER_S_5 = "\\b(twenty|thirty|forty|fifty|sixty|seventy|eighty|ninety)[-\\s]+(one|two|three|four|five|six|seven|eight|nine)\\b";
  
  private static String numberRegExp() {
    StringBuilder sb = new StringBuilder();
    sb.append('(' + NUMBER_S_3 + "\\s+" + NUMBER_S_1 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_1 + "\\s+" + NUMBER_S_4 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_2 + "\\s+" + NUMBER_S_4 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_3 + "\\s+" + NUMBER_S_4 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_1 + "\\s+" + NUMBER_S_1 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_5 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_1 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_2 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_3 + ')');
    sb.append("|");
    sb.append('(' + NUMBER_S_4 + ')');
    return sb.toString();
  }

  /**
   * Creates a number pattern.
   * @return a pattern.
   */
  public static Pattern number() {
    return Pattern.compile(numberRegExp());
  }

  /**
   * Creates a height pattern.
   * @return a pattern
   */
  public static Pattern height() {
    StringBuilder sb = new StringBuilder();
    sb.append("(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)\\s+(feet|ft\\.?)\\s+(\\d+|one|two|three|four|five|six|seven|eight|nine|ten)\\s+(inches|inch|in\\.?)");
    sb.append("|");
    sb.append("\\d+\\s?(feet|ft\\.?)\\s+\\d+\\s?(inches|inch|in\\.?)");
    sb.append("|");
    sb.append("(");
    sb.append(numberRegExp());
    sb.append(")");
    sb.append("\\s+");
    sb.append("(in\\s+)?");
    sb.append("\\b([Ii]n|inches|inch|M|m|[Cc]m|[Mm]eters?|[Cc]entimeters?|feet|foot|ft)\\b");
    sb.append("|");
    sb.append("\\d+'([,\\s])?\\d+(\"|'')");
    return Pattern.compile(sb.toString());
  }

  /**
   * Creates a weight pattern.
   * @return a pattern
   */
  public static Pattern weight() {
    StringBuilder sb = new StringBuilder();
    sb.append('(' + numberRegExp() + ')' + "\\s+([Kk]g|[Ll]bs|[Kk]ilograms?|[Kk]ilos?|[Pp]ounds?)\\b");
    return Pattern.compile(sb.toString());
  }

  /**
   * Creates an age pattern
   * @return a pattern
   */
  public static Pattern age() {
    StringBuilder sb = new StringBuilder();
    sb.append("(\\d+|");
    sb.append('(' + numberRegExp() + ')');
    sb.append("|");
    sb.append("(early|late)\\s(twenties|thirties|forties|fifties|sixties|seventies|eighties|nineties)");
    sb.append(')');
    sb.append("(\\s+years\\s+old|\\-year\\-old)?\\b");
    return Pattern.compile(sb.toString());
  }
}
