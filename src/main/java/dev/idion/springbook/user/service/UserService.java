package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface UserService {

  void add(User user);

  @Transactional(readOnly = true)
  User get(String id);

  @Transactional(readOnly = true)
  List<User> getAll();

  void deleteAll();

  void update(User user);

  void upgradeLevels();
}
