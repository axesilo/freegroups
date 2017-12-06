package com.axesilo.research.freegroups;

import java.util.*;

/**
 * A Levels object represents a succession of levels in a Cayley graph with respect to a given
 * generating set.  Generators cannot be changed once the Levels object is created.  Inverses
 * are NOT automatically included---appending inverses to the generating set is the
 * responsibility of the caller.
 * <p>
 * For efficiency, Levels does not track connections between elements (i.e. it is not a graph).
 * Use the class CayleyGraph to generate actual graphs and Cayley tables.
 */
public class Levels {
  public static Levels generate(List<Word> generators, int numberOfLevels) {
    Levels levels = new Levels(generators);
    levels.generateUpTo(numberOfLevels);
    return levels;
  }

  public static Levels generate(Word[] generators, int numberOfLevels) {
    return generate(Arrays.asList(generators), numberOfLevels);
  }

  private final Word[] generators;
  private List<List<Word>> levels;  // outer list will be contiguous levels
  private Map<Word, Integer> seen;  // all the words seen so far and their levels

  /**
   * Create a new Levels object.  The constructor does not actually generate any levels.
   *
   * @param generators A list of Words to add as generators.  The constructor copies the
   *                   list into a new array to avoid entanglement with future modifications
   *                   to the list.
   */
  protected Levels(List<Word> generators) {
    this.generators = generators.toArray(new Word[generators.size()]);
    levels = new ArrayList<>();
    seen = new HashMap<>();
  }

  /**
   * Ensure levels have been generated up to a given stopping point.
   *
   * @param lastLevel The last level (inclusive) whose existence is to be guaranteed.
   */
  public void generateUpTo(final int lastLevel) {
    if (levels.size() == 0) {
      List<Word> levelZero = new ArrayList<>();
      levelZero.add(Word.id);
      seen.put(Word.id, 0);

      levels.add(levelZero);
    }

    while (levels.size() < lastLevel + 1) {
      generateNextLevel();
    }
  }

  /*
   * Generate the next available level.  This method assumes at least one level has
   * already been created.
   */
  private void generateNextLevel() {
    int currentLevelNumber = levels.size() - 1;
    List<Word> currentLevel = levels.get(currentLevelNumber);
    List<Word> nextLevel = new ArrayList<>();

    for (Word w : currentLevel) {
      for (Word g : generators) {
        Word newWord = w.multiply(g);
        if (!seen.containsKey(newWord)) {
          nextLevel.add(newWord);
          seen.put(newWord, currentLevelNumber + 1);
          connect(w, currentLevelNumber, newWord, currentLevelNumber + 1, g, true);
        } else {
          connect(w, currentLevelNumber, newWord, seen.get(newWord), g, false);
        }
      }
    }

    levels.add(nextLevel);
  }

  /**
   * This method listens for when new connections are found between level sets (especially
   * when new elements are added to a level set.)
   * <p>
   * The only element addition that does not trigger this method is the adding of the
   * identity to the zeroth level set.
   */
  protected void connect(Word parent, int parentLevel,
                         Word child, int childLevel,
                         Word edge, boolean isNew) {
    // do nothing
  }

  /**
   * Get level sizes for all available levels.
   *
   * @return level sizes starting from the 0th level
   */
  public int[] getLevelSizes() {
    int[] sizes = new int[levels.size()];
    for (int i = 0; i < sizes.length; i++) {
      sizes[i] = levels.get(i).size();
    }
    return sizes;
  }

  /**
   * Get level ratios for all available levels.
   *
   * @return ratios, starting from L1 / L0
   */
  public double[] getLevelRatios() {
    int[] sizes = getLevelSizes();
    double[] ratios = new double[sizes.length - 1];
    for (int i = 1; i < sizes.length; i++) {
      ratios[i - 1] = (double) sizes[i] / sizes[i - 1];
    }
    return ratios;
  }
}
