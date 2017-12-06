package com.axesilo.research.freegroups;

public enum OutputFormat {
  Text, Latex, HTML;

  public static OutputFormat getFormat(String text) {
    String textLower = text.toLowerCase();
    switch (textLower) {
      case "txt":
      case "text":
      case "plain-text":
        return Text;
      case "tex":
      case "latex":
        return Latex;
      case "html":
        return HTML;
      default:
        throw new IllegalArgumentException("invalid output format: " + text);
    }
  }
}
