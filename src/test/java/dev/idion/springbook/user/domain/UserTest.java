package dev.idion.springbook.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserTest {

  User user;

  @BeforeEach
  void setUp() {
    user = new User();
  }

  @Test
  void upgradeLevel() {
    Level[] levels = Level.values();
    for (Level level : levels) {
      if (level.nextLevel() == null) {
        continue;
      }
      user.setLevel(level);
      user.upgradeLevel();
      assertThat(user.getLevel()).isEqualByComparingTo(level.nextLevel());
    }
  }

  @Test
  void cannotUpgradeLevel() {
    assertThatThrownBy(() -> {
      Level[] levels = Level.values();
      for (Level level : levels) {
        if (level.nextLevel() != null) {
          continue;
        }
        user.setLevel(level);
        user.upgradeLevel();
      }
    }).isInstanceOf(IllegalStateException.class);
  }
}
