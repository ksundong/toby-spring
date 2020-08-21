package dev.idion.springbook.user.domain;

public enum Level {
  GOLD(null), SILVER(GOLD), BASIC(SILVER);

  private final Level next;

  Level(Level next) {
    this.next = next;
  }

  public Level nextLevel() {
    return this.next;
  }
}
