package dev.idion.springbook.user.dao;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "dev.idion.springbook.user")
@EnableTransactionManagement
public class DaoFactory {

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost:3306/springbook");
    dataSource.setUsername("spring");
    dataSource.setPassword("book");

    return dataSource;
  }

  @Bean
  public JdbcTemplate jdbcTemplate() {
    return new JdbcTemplate(dataSource());
  }

  @Bean
  public PlatformTransactionManager txManager() {
    return new DataSourceTransactionManager(dataSource());
  }

  @Bean
  public MailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("mail.ksug.org");
    mailSender.setDefaultEncoding("UTF-8");
    return mailSender;
  }

  @Bean
  public Map<String, String> sqlMap() {
    Map<String, String> sqlMap = new HashMap<>();
    sqlMap.put("userAdd",
        "insert into USER(id, name, password, login, recommend, level, email) VALUES (?,?,?,?,?,?,?)");
    sqlMap.put("userGet", "select * from USER where id = ?");
    sqlMap.put("userDeleteAll", "delete from USER");
    sqlMap.put("userGetCount", "select count(*) from USER");
    sqlMap.put("userGetAll", "select * from USER order by id");
    sqlMap.put("userUpdate",
        "update USER set name = ?, password = ?, login = ?, recommend = ?, level = ?, email = ? where id = ?");
    return sqlMap;
  }
}
