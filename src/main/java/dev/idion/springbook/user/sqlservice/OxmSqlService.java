package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.sqlservice.exception.SqlRetrievalFailureException;
import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import dev.idion.springbook.user.sqlservice.reader.SqlReader;
import dev.idion.springbook.user.sqlservice.registry.SqlRegistry;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

@Primary
@Service
public class OxmSqlService implements SqlService {

  private final BaseSqlService baseSqlService;
  private final OxmSqlReader oxmSqlReader;
  private final SqlRegistry sqlRegistry;

  public OxmSqlService(BaseSqlService baseSqlService, SqlRegistry sqlRegistry,
      Unmarshaller unmarshaller, Resource sqlmap) {
    this.baseSqlService = baseSqlService;
    this.oxmSqlReader = new OxmSqlReader(unmarshaller, sqlmap);
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.baseSqlService.setSqlReader(this.oxmSqlReader);
    this.baseSqlService.setSqlRegistry(this.sqlRegistry);
    this.baseSqlService.loadSql();
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    return this.baseSqlService.getSql(key);
  }

  private static class OxmSqlReader implements SqlReader {

    private final Unmarshaller unmarshaller;
    private final Resource sqlmap;

    private OxmSqlReader(Unmarshaller unmarshaller, Resource sqlmap) {
      this.unmarshaller = unmarshaller;
      this.sqlmap = sqlmap;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
      try {
        Source source = new StreamSource(sqlmap.getInputStream());
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

        for (SqlType sqlType : sqlmap.getSql()) {
          sqlRegistry.registerSql(sqlType.getKey(), sqlType.getValue());
        }
      } catch (IOException e) {
        throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다.");
      }
    }
  }
}
