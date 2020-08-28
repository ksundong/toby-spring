package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.service.DummyMailSender;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "dev.idion.springbook.user", excludeFilters = {
    @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {DaoFactory.class})
})
@EnableTransactionManagement
public class TestDaoFactory {

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    dataSource.setDriverClass(com.mysql.cj.jdbc.Driver.class);
    dataSource.setUrl("jdbc:mysql://localhost:3306/testdb");
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
    return new DummyMailSender();
  }

  @Bean
  public Unmarshaller unmarshaller() {
    Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
    unmarshaller.setContextPath("dev.idion.springbook.user.sqlservice.jaxb");
    return unmarshaller;
  }

  @Bean
  public String sqlmapFile() {
    return "/userdao-sqlmap.xml";
  }
}
