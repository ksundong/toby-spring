package dev.idion.springbook.user.service;

import dev.idion.springbook.user.domain.User;
import java.util.List;
import org.springframework.mail.SimpleMailMessage;

public interface UserService {

  void upgradeLevels();

  void upgradeLevels(List<SimpleMailMessage> mailMessages);

  void add(User user);
}
