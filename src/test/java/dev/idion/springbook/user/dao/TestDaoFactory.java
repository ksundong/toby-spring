package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.service.DummyMailSender;
import java.sql.Driver;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
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
@PropertySource("/database.properties")
@EnableTransactionManagement
public class TestDaoFactory {

  private final Environment env;

  public TestDaoFactory(Environment env) {
    this.env = env;
  }

  @Bean
  public DataSource dataSource() {
    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

    try {
      dataSource.setDriverClass(
          (Class<? extends Driver>) Class.forName(env.getProperty("db.driverClass")));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    dataSource.setUrl(env.getProperty("db.url"));
    dataSource.setUsername(env.getProperty("db.username"));
    dataSource.setPassword(env.getProperty("db.password"));

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
  public Resource sqlmap() {
    return new ClassPathResource("sqlmap.xml", UserDao.class);
  }
}
