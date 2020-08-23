package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;

public interface UserService {

  void upgradeLevels();

  void add(User user);
}
