package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;

public interface UserDao {

  void add(User user) throws DuplicateKeyException;

  User get(String id);

  void deleteAll();

  Integer getCount();

  List<User> getAll();
}
