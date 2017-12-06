package com.axesilo.research.freegroups;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Word represents a reduced word in a free group.  Words are immutable.
 */
public final class Word {
  public static final Word id = parse("");
  public static final Word x = parse("x");
  public static final Word y = parse("y");

  /**
   * Create a reduced word out of a textual representation (e.g. "xyX", "x2y3y-2", "").
   * Uppercase letters are treated as inverses.
   * <p>
   * This method does error checking, which may hinder performance (in contrast to, e.g.,
   * multiply()).
   *
   * @param text the word to parse
   * @return a reduced Word version of <tt>text</tt>
   */
  public static Word parse(String text) {
    if (text == null) {
      throw new IllegalArgumentException("Cannot parse null pointer as word");
    }
    Deque<Character> letterList = new ArrayDeque<>();
    Deque<Integer> exponentList = new ArrayDeque<>();
    Pattern factor = Pattern.compile("([a-zA-Z])([-+]?\\d*)");
    Matcher matcher = factor.matcher(text);

    int lastEnd = 0;
    while (matcher.find()) {
      if (matcher.start() != lastEnd) {
        throw new IllegalArgumentException("Word malformed at position " + lastEnd + ": " + text);
      }
      lastEnd = matcher.end();
      char letter = matcher.group(1).charAt(0);
      String exponentString = matcher.group(2);
      int exponent = exponentString.isEmpty() ? 1 : Integer.parseInt(exponentString);

      reduceAppend(letterList, exponentList, letter, exponent);
    }
    if (lastEnd != text.length()) {
      throw new IllegalArgumentException("Word has extra garbage on the end: " + text);
    }

    return new Word(letterList, exponentList, true);
  }

  private static void reduceAppend(Deque<Character> letterList, Deque<Integer> exponentList,
                                   char letter, int exponent) {
    // Don't add "zero" factors
    if (exponent == 0) {
      return;
    }

    // Uppercase letters are to denote inverses
    if (Character.isUpperCase(letter)) {
      letter = Character.toLowerCase(letter);
      exponent = -exponent;
    }

    // Check if collapsing can happen
    if (!letterList.isEmpty() && letterList.peek() == letter) {
      int newExponent = exponentList.pop() + exponent;
      if (newExponent == 0) {
        letterList.pop();
      } else {
        exponentList.push(newExponent);
      }
    } else {
      letterList.push(letter);
      exponentList.push(exponent);
    }
  }


  // letters must be all lowercase in the internal representation.  Also, "reduced word" rules
  // are followed: no identical adjacent letters, no 0 exponent.s
  private final char[] letters;
  private final int[] exponents;

  private Word() {
    this(new char[0], new int[0]);
  }

  private Word(char[] letters, int[] exponents) {
    this.letters = letters;
    this.exponents = exponents;
  }

  private Word(Collection<Character> letterCollection, Collection<Integer> exponentCollection,
               boolean reverse) {
    int size = letterCollection.size();
    letters = new char[size];
    exponents = new int[size];

    // Copy collections into arrays
    Iterator<Character> letterIterator = letterCollection.iterator();
    Iterator<Integer> exponentIterator = exponentCollection.iterator();
    int i = reverse ? size - 1 : 0;
    while (reverse && i >= 0 || !reverse && i < size) {
      letters[i] = letterIterator.next();
      exponents[i] = exponentIterator.next();
      i = i + (reverse ? -1 : 1);
    }
  }

  public Word multiply(Word other) {
    Deque<Character> newLetters = new ArrayDeque<>();
    Deque<Integer> newExponents = new ArrayDeque<>();
    for (char c : letters) {
      newLetters.push(c);
    }
    for (int i : exponents) {
      newExponents.push(i);
    }

    for (int i = 0; i < other.letters.length; i++) {
      reduceAppend(newLetters, newExponents, other.letters[i], other.exponents[i]);
    }

    return new Word(newLetters, newExponents, true);
  }

  public Word inverse() {
    int len = letters.length;
    char[] newLetters = new char[len];
    int[] newExponents = new int[len];
    for (int i = 0; i < len; i++) {
      newLetters[len - i - 1] = letters[i];
      newExponents[len - i - 1] = -exponents[i];
    }
    return new Word(newLetters, newExponents);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Word word = (Word) o;

    if (!Arrays.equals(letters, word.letters)) return false;
    return Arrays.equals(exponents, word.exponents);
  }

  /**
   * Return true if this word equals the reduced word defined by a parallel pair of arrays of
   * letters and exponents, false otherwise.
   * <p>
   * If the other word is not reduced, the behavior of this function is unspecified.  This
   * function is mainly used for testing purposes.
   *
   * @param otherLetters   letters in the word to check
   * @param otherExponents exponents corresponding to letters in the word to check
   * @return true if and only if this word equals the other word
   */
  public boolean equalsByArrays(final char[] otherLetters, final int[] otherExponents) {
    return Arrays.equals(letters, otherLetters) && Arrays.equals(exponents, otherExponents);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(letters);
    result = 31 * result + Arrays.hashCode(exponents);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < letters.length; i++) {
      builder.append(letters[i]);
      if (exponents[i] != 1) {
        builder.append(exponents[i]);
      }
    }
    return builder.length() > 0 ? builder.toString() : "1";
  }

  public String toLatexString() {
    return "$" + toStringWithDigitModifier("^{$0}") + "$";
  }

  public String toHTMLString() {
    return toStringWithDigitModifier("<sup>$0</sup>");
  }

  private String toStringWithDigitModifier(String modifierRegex) {
    String ordinaryString = toString();
    if (Objects.equals(ordinaryString, "1")) { return "1"; }

    Pattern digitPattern = Pattern.compile("[-+]?\\d+");
    return ordinaryString.replaceAll("[-+]?\\d+", modifierRegex);
  }

  public String toString(OutputFormat format) {
    switch (format) {
      case Text:
        return toString();
      case Latex:
        return toLatexString();
      case HTML:
        return toHTMLString();
      default:
        throw new UnsupportedOperationException("unsupported format: " + format);
    }
  }
}
