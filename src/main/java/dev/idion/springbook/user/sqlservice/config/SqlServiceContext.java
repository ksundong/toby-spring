package dev.idion.springbook.user.sqlservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class SqlServiceContext {

  private final SqlMapConfig sqlMapConfig;

  public SqlServiceContext(SqlMapConfig sqlMapConfig) {
    this.sqlMapConfig = sqlMapConfig;
  }

  @Bean
  public Unmarshaller unmarshaller() {
    Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
    unmarshaller.setContextPath("dev.idion.springbook.user.sqlservice.jaxb");
    return unmarshaller;
  }

  @Bean
  public Resource sqlmap() {
    return this.sqlMapConfig.getMapResource();
  }

  @Bean
  public EmbeddedDatabase embeddedDatabase() {
    return new EmbeddedDatabaseBuilder()
        .setType(EmbeddedDatabaseType.H2)
        .addScript("classpath:/schema.sql")
        .build();
  }
}
