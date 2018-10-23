package ai.vitk.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by phuonglh on 11/11/16.
 * <p>
 * Detection of token shape using regular expressions.
 * </p>
 */

public class TokenShape {
  static final Pattern ALLCAP = Pattern.compile("[\\p{Lu}]{2,}");
  static final Pattern NAME = Pattern.compile("\\b(\\p{Lu}\\p{Ll}+)([\\s+_&\\-]?(\\p{Lu}\\p{Ll}+))+\\b");
  static final Pattern CAPITAL = Pattern.compile("\\b[\\p{Lu}]+[\\p{Ll}_]*[+]?");
  static final Pattern URL = Pattern.compile("(((?:\\b\\w+)\\://)+[a-zA-z][\\-\\w]*\\w+(\\.\\w[\\-\\w]*)+(/[\\w\\-]+)*(\\.\\w+)?(/?)(\\?(\\w+=[\\w%]+))*(&(\\w+=[\\w%]+))*|[a-z]+((\\.)\\w+)+)");
  static final Pattern EMAIL = Pattern.compile("(\\w[-._%:\\w]*@\\w[-._\\w]*\\w\\.\\w{2,3})");
  static final Pattern PHONE = Pattern.compile("\\b(\\(?\\d{1,3}\\)?)*(-\\(?\\d{1,4}\\)?)+(-?\\d)+\\b|\\b\\d{8,10}\\b");
  static final Pattern DATE_1 = Pattern.compile("\\b(([12][0-9]|3[01]|0*[1-9])[-/.](1[012]|0*[1-9])[-/.](\\d{4}|\\d{2})|(1[012]|0*[1-9])[-/.]([12][0-9]|3[01]|0*[1-9])[-/.](\\d{4}|\\d{2})|([12][0-9]|3[01]|0*[1-9])[-/.](1[012]|0*[1-9]))\\b");
  static final Pattern DATE_2 = Pattern.compile("\\b(1[012]|0*[1-9])[-/.](\\d{4}|\\d{2})\\b");
  static final Pattern DATE_3 = Pattern.compile("\\b([12][0-9]|3[01]|0*[1-9])[-/.](1[012]|0*[1-9])\\b");
  static final Pattern TIME = Pattern.compile("\\b(\\d{1,2}:\\d{1,2})(:\\d{1,2})?\\b");
  static final Pattern ORDER = Pattern.compile("\\b(\\d+(st|nd|rd|th))\\b");
  
  static final Pattern FRACTION = Pattern.compile("\\b[0-9a-z]+/\\d+");
  static final Pattern NUMBER = Pattern.compile("([+-]?([0-9]*)?[0-9]+([.,]\\d+)*)\\b");
  static final Pattern WEIGHT = Pattern.compile("([+-]?([0-9]*)?[0-9]+([.,]\\d+)*)\\s?([Kk]g|lbs?|kilograms?|kilos?|pounds?)\\b");
  static final Pattern HEIGHT = Pattern.compile("([+-]?([0-9]*)?[0-9]+([.,]\\d+)*)\\s?([Ii]n|inches|inch|M|m|[Cc]m|[Mm]eters?|[Cc]entimeters?|feet|foot|ft)\\b");
  static final Pattern ACCOUNT = Pattern.compile("\\w*@\\w+");
  static final Pattern PERCENTAGE = Pattern.compile("([+-]?([0-9]*[.,])?[0-9]+%)");
  static final Pattern PHRASE = Pattern.compile("\\b[\\p{Ll}\\s_]+(?=[\\s.,!?:;\"“”'‘)(-]+|$)(?<!(https?|ftp|git))");
  static final Pattern PUNCTUATION = Pattern.compile("[\\p{Punct}“”’‘…–-]+");
  static final Pattern CURRENCY = Pattern.compile("\\p{Sc}+\\s?([0-9]*)?[0-9]+([.,]\\d+)*\\b");
  static final Pattern MIX = Pattern.compile("\\b(\\p{Alpha}+\\p{Digit}+\\p{Alpha}*)|(\\p{Digit}+\\p{Alpha}+\\p{Digit}*)|(\\p{Digit}+[A-Z]+)\\b");
  static final Pattern APPLE = Pattern.compile("\\b(\\p{Ll}+\\p{Lu}+\\p{Alnum}*)\\b");
  static final Pattern ACRONYM = Pattern.compile("\\b(\\p{Lu}\\p{Ll}?\\.)+?!$");
  static final Pattern ABBREVIATION = Pattern.compile("\\b(\\p{Lu}(\\.\\p{Lu})+\\.?)\\b");
  static final Pattern HYPHEN = Pattern.compile("\\b\\p{Alpha}+[-]\\p{Alnum}+\\b");
  
  static final Pattern BLOOD = Pattern.compile("\\b(AB|A|B|O)((\\s+(plus|minus|positive|negative))|[+-])?(?![\\w])");
  static final Pattern HONOR = Pattern.compile("\\b(Mrs|Mr|Ms|Dr|PhD|MD|Prof|Mdm|Madame|Madam|Sir|Miss|Mx|Pr|Br|Haji|Lord|Dame|General|Judge|Prince)\\.?(?![\\w])");
  static final Pattern AUXILIARY = Pattern.compile("('(d|ll|m|re|s|t|ve)|(n't))\\b");

  static final Pattern ENTITY = Pattern.compile("\\b(\\d+[a-z]*([-/]\\d+)*[,\\s]+)?(P\\.?O\\.?|(\\p{Lu}|\\d)+[\\p{Ll}.-]*)([,\\s]+((of the|of|da|u\\.?|-)\\s)?(\\p{Lu}(\\.\\p{Lu})+\\.?|(\\p{Lu}|\\d|&)+[\\p{Ll}.\\d-]+|\\d+([-]\\d+)*))+([,\\s]+\\d+([-]\\d+)*)?([,\\s]+[A-Z]+)?([,\\s]+\\d+)?\\b");
  static final Pattern DATE_ST = Pattern.compile("\\b(\\d+(th|rd|nd|st)?[, ]+)?(January|Jan\\.?|February|Feb\\.?|March|Mar\\.?|April|Apr\\.?|May|June|Jun\\.?|July|Jul\\.?|August|Aug\\.?|September|Sep\\.?|October|Oct\\.?|November|Nov\\.?|December|Dec\\.?)([, ]+\\d+([, ]+\\d+)?)?\\b");
  static final Pattern COLOR = Pattern.compile("\\b((cold|medium|deep|fresh|mellow|faded|pale|dusty|jade|dark|light|topaz|satin|sugar|strawberry)\\s+)?(black|red|green|blue|yellow|blonde?|magenta|grey|gray|brown|hazel|copper|emerald|honey|pink|white|purple|orange|violate|amber|brunette|ombre)(ish)?\\b");
  static final Pattern DEGREE = Pattern.compile("\\b(bachelor|master|doctor|doctoral|doctorate|phd)\\b");
  static final Pattern AND = Pattern.compile("\\b([A-Z]+[&_\\-][A-Z]+)\\b");
  
  public static final List<ai.vitk.type.Pattern> COMMON_PATTERNS = new ArrayList<>();
  static {
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("NAME", NAME, 2));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("ALLCAP", ALLCAP, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("CAPITAL", CAPITAL, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("DATE_1", DATE_1, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("DATE_2", DATE_2, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("DATE_3", DATE_3, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("TIME", TIME, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("PERCENTAGE", PERCENTAGE, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("NUMBER", NUMBER, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("FRACTION", FRACTION, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("URL", URL, 3));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("EMAIL", EMAIL, 4));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("ACCOUNT", ACCOUNT, 3));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("CURRENCY", CURRENCY, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("MIX", MIX, 2));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("APPLE", APPLE, 2));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("ACRONYM", ACRONYM, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("ABBREV", ABBREVIATION, 2));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("HYPHEN", HYPHEN, 1));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("PUNCT", PUNCTUATION, 0));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("AND", AND, 4));
    COMMON_PATTERNS.add(new ai.vitk.type.Pattern("PHRASE", PHRASE, 0));
  }
  
  public static final List<ai.vitk.type.Pattern> EXTENDED_PATTERNS = new ArrayList<>();
  static {
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("PHONE", PHONE, 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("PERCENTAGE", PERCENTAGE, 0));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("WEIGHT", WEIGHT, 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("HEIGHT", HEIGHT, 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("NUMBER", NUMBER, 0));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("ORDER", ORDER, 1));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("HONOR", HONOR, 4));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("AUX", AUXILIARY, 1));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("ENTITY", ENTITY, 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("BLOOD", BLOOD, 2));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("APPLE", APPLE, 0));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("NUMBER_ST", Units.number(), 2));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("HEIGHT_ST", Units.height(), 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("WEIGHT_ST", Units.weight(), 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("AGE", Units.age(), 3));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("DATE_ST", DATE_ST, 4));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("COLOR", COLOR, 1));
    EXTENDED_PATTERNS.add(new ai.vitk.type.Pattern("DEGREE", DEGREE, 1));
    EXTENDED_PATTERNS.addAll(COMMON_PATTERNS);
  }

  /**
   * Detects and returns the shape of a given token or an empty string
   * if the token does not have any special shape (number, date, weight, etc.)
   *
   * @param token a token to match
   * @return shape name or an empty string if the input token does not have
   * any particular shape.
   */
  public static String shape(String token) {
    return shape(token, COMMON_PATTERNS);
  }

  /**
   * Detects and returns the shape of a given token or an empty string
   * if the token does not have any special shape (number, date, weight, etc.).
   * The considered token patterns are defined in a given list.
   * 
   * @param token a token to match
   * @param patterns a list of patterns             
   * @return the shape of the token
   * */
  public static String shape(String token, List<ai.vitk.type.Pattern> patterns) {
    for (ai.vitk.type.Pattern p : patterns) 
      if (!p.getName().equals("PHRASE") && p.getPattern().matcher(token).matches())
        return p.getName();
    return "";
  }
}
