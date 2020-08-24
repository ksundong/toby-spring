package dev.idion.springbook.user.dao;

import dev.idion.springbook.user.service.TransactionAdvice;
import dev.idion.springbook.user.service.UserService;
import javax.sql.DataSource;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@ComponentScan(basePackages = "dev.idion.springbook.user")
public class DaoFactory {

  private final UserService userServiceImpl;

  public DaoFactory(UserService userServiceImpl) {
    this.userServiceImpl = userServiceImpl;
  }

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
  public TransactionAdvice transactionAdvice() {
    return new TransactionAdvice(txManager());
  }

  @Bean
  public NameMatchMethodPointcut transactionPointcut() {
    NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
    pointcut.setMappedName("upgrade*");
    return pointcut;
  }

  @Bean
  public DefaultPointcutAdvisor transactionAdvisor() {
    return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvice());
  }

  @Bean
  public ProxyFactoryBean userService() {
    ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
    proxyFactoryBean.setTarget(userServiceImpl);
    proxyFactoryBean.addAdvisor(transactionAdvisor());
    return proxyFactoryBean;
  }
}
