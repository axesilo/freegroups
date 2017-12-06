package com.axesilo.research.freegroups;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class with all static methods that acts as an API for the library functionality.
 * <p>
 * Decisions about behavior in this class are made from the client-side perspective (e.g.
 * usually generator inverses are automatically included, data are returned as Lists, etc.).
 */
public final class Calculate {

  /**
   * Get a list of successive ratios of spherical growth values with respect to a given
   * generating set.  Inverses are automatically included.
   * <p>
   * For example, ratios(["x", "y"], 3) will return [4.0, 3.0, 3.0], since the spherical growth
   * values of F2 with respect to {x, y} for levels 0--3 are 1, 4, 12, 36.
   *
   * @param generatingSet  List of generators as Strings.  Inverses will automatically be
   *                       included.
   * @param numberOfLevels Number of levels to compute (at least one)
   * @return A list of successive ratios: [ sigma(1) / sigma(0),  sigma(2) / sigma(1), ... ]
   */
  public static List<Double> ratios(List<String> generatingSet, int numberOfLevels) {
    List<Word> generatingWords = parseWords(generatingSet, true);

    Levels levels = Levels.generate(generatingWords, numberOfLevels);
    double[] ratioArray = levels.getLevelRatios();
    return Arrays.stream(ratioArray).boxed().collect(Collectors.toList());
  }

  /**
   * Get a level-2 Cayley table with respect to a given generating set.  Inverses can be
   * included or excluded.  The output format  should be conform to the options in the
   * OutputFormat class; examples of valid formats include "text", "latex", and "html".
   *
   * @param generatingSet   List of generators as strings
   * @param format          Output format (e.g. "text", "latex", "html")
   * @param includeInverses Whether or not to include inverses of the generators
   * @return
   */
  public static String cayleyTable(List<String> generatingSet, String format, boolean
      includeInverses) {
    OutputFormat formatType = OutputFormat.getFormat(format);
    if (formatType != OutputFormat.HTML) {
      throw new UnsupportedOperationException("only HTML tables are available at the moment");
    }

    return CayleyTable.symmetricTable(parseWords(generatingSet, includeInverses)).toHTMLString();
  }

  /*
   * Add all inverses of the generators in a list to that same list (if they are not already
   * included).  For example, calling addInverses on [x, y, x^(-1)] will modify the list so
   * that it contains [x, y, x^(-1), y^(-1)].
   */
  private static void addInverses(List<Word> words) {
    int originalSize = words.size();
    for (int i = 0; i < originalSize; i++) {
      Word inverse = words.get(i).inverse();
      if (!words.contains(inverse)) {
        words.add(inverse);
      }
    }
  }

  /**
   * Take in a list of Strings and parse each one as a Word object, including inverses
   * if necessary.
   *
   * @param words           an array of Strings to parse as words
   * @param includeInverses whether or not inverses should automatically be included
   */
  public static List<Word> parseWords(List<String> words, boolean includeInverses) {
    List<Word> wordList = words.stream().map(Word::parse).collect(Collectors.toList());
    if (includeInverses) {
      addInverses(wordList);
    }
    return wordList;
  }

  public static void writeHTMLTable(List<String> generators, String filename) {
    List<Word> words = parseWords(generators, true);

    // TODO work on this.  getResource takes paths relative to the "root."  So they're in the
    // out/production/classes directory right now, which isn't optimal.

    try (PrintWriter writer = new PrintWriter(filename);
        BufferedReader headerReader = new BufferedReader(new InputStreamReader(
            Calculate.class.getClassLoader().getResourceAsStream("header.html")));
        BufferedReader footerReader = new BufferedReader(new InputStreamReader(
            Calculate.class.getClassLoader().getResourceAsStream("footer.html")))) {

      headerReader.lines().forEach(writer::println);
      writer.println(CayleyTable.symmetricTable(words).toHTMLString());
      footerReader.lines().forEach(writer::println);

      System.out.println("Successfully wrote to " + filename + ".");
    } catch (IOException e) {
      System.out.println("There was an error writing to the file.");
    }
  }

  private Calculate() {
    throw new AssertionError();  // static class; not meant to be instantiated
  }
}
