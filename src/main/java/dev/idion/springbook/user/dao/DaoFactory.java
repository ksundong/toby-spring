package dev.idion.springbook.user.dao;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

@ComponentScan(basePackages = "dev.idion.springbook.user")
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
}
