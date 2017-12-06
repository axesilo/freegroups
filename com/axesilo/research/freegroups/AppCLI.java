package com.axesilo.research.freegroups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppCLI {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("AppCLI generates statistics for a single generating set.\n" +
          "Example usage: 'java AppCLI 5 x y xy' will generate 5 levels\n" +
          "using the generating set {x, y, xy}.");
      return;
    }

    int numLevels = Integer.parseInt(args[0]);
    List<String> actualWords = new ArrayList<>();
    for (int i = 0; i < args.length - 1; i++) {
      actualWords.add(args[i + 1]);
    }
    calculateLevels(actualWords, numLevels);
    // Calculate.writeHTMLTable(actualWords, "out.html");

  }

  private static void calculateLevels(List<String> words, int numLevels) {
    Levels levels = Levels.generate(Calculate.parseWords(words, true), numLevels);
    System.out.println("Level sizes: " + Arrays.toString(levels.getLevelSizes()));
    System.out.println("Level ratios: " + Arrays.toString(levels.getLevelRatios()));
    System.out.println("Cayley table:");
    System.out.println(Calculate.cayleyTable(words, "html", true));
  }
}
