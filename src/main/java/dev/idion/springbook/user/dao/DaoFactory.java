package dev.idion.springbook.user.dao;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@ComponentScan(basePackages = "dev.idion.springbook.user")
@EnableAspectJAutoProxy
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
  public TransactionInterceptor transactionAdvice() {
    Properties properties = new Properties();
    properties.put("get*", "PROPAGATION_REQUIRED,readOnly,timeout_30");
    properties.put("upgrade*", "PROPAGATION_REQUIRES_NEW,ISOLATION_SERIALIZABLE");
    properties.put("*", "PROPAGATION_REQUIRED");

    NameMatchTransactionAttributeSource txSource = new NameMatchTransactionAttributeSource();
    txSource.setProperties(properties);

    return new TransactionInterceptor((TransactionManager) txManager(), txSource);
  }
}
