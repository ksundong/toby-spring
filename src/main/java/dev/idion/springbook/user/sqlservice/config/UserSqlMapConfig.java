package dev.idion.springbook.user.sqlservice.config;

import dev.idion.springbook.user.dao.UserDao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class UserSqlMapConfig implements SqlMapConfig {

  @Override
  public Resource getMapResource() {
    return new ClassPathResource("sqlmap.xml", UserDao.class);
  }
}
