package com.axesilo.research.freegroups;

import java.util.List;

public class CayleyGraph extends Levels {
  protected CayleyGraph(List<Word> generators) {
    super(generators);
  }

  @Override
  protected void connect(Word parent, int parentLevel, Word child, int childLevel,
                         Word edge, boolean isNew) {
  }
}
