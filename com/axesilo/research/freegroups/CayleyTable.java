package com.axesilo.research.freegroups;

import java.util.*;

public class CayleyTable {
  /** Return a Cayley table whose row headers are the same as its column headers. */
  public static CayleyTable symmetricTable(List<Word> words) {
    return new CayleyTable(words, words);
  }

  /** Return a Cayley table that uses the specified lists of row headers and column headers. */
  public static CayleyTable table(List<Word> rowHeaders, List<Word> columnHeaders) {
    return new CayleyTable(rowHeaders, columnHeaders);
  }

  private Word[] rowHeaders;
  private Word[] columnHeaders;
  private Word[][] table;

  private CayleyTable(List<Word> rowHeaders, List<Word> columnHeaders) {
    this.rowHeaders = rowHeaders.toArray(new Word[rowHeaders.size()]);
    this.columnHeaders = columnHeaders.toArray(new Word[columnHeaders.size()]);
    buildTable();
  }

  private synchronized void buildTable() {
    int rows = rowHeaders.length;
    int cols = columnHeaders.length;
    table = new Word[rows][cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        table[i][j] = rowHeaders[i].multiply(columnHeaders[j]);
      }
    }
  }

  public Word getEntry(int i, int j) {
    return table[i][j];
  }

  public int getHeight() {
    return rowHeaders.length;
  }

  public int getWidth() {
    return columnHeaders.length;
  }

  public String toHTMLString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<table>\n");
    appendTableHeader(builder);
    for (int i = 0; i < table.length; i++) {
      appendTableRow(builder, i);
    }
    builder.append("</table>");
    return builder.toString();
  }

  private void appendTableHeader(StringBuilder builder) {
    builder.append("<tr><th></th>");
    for (Word word : columnHeaders) {
      builder.append("<th scope=\"col\">").append(word.toHTMLString()).append("</th>");
    }
    builder.append("</tr>\n");
  }

  private void appendTableRow(StringBuilder builder, int rowNumber) {
    builder.append("<tr><th scope=\"row\">").append(rowHeaders[rowNumber].toHTMLString())
        .append("</th>");
    for (Word word : table[rowNumber]) {
      builder.append("<td>").append(word.toHTMLString()).append("</td>");
    }
    builder.append("</tr>\n");
  }
}