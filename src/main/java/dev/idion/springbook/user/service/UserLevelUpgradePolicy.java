package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;

public interface UserLevelUpgradePolicy {

  boolean canUpgradeLevel(User user);

  void upgradeLevel(User user);
}
