package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.IOException;
import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.springframework.context.annotation.Primary;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Service;

@Primary
@Service
public class OxmSqlService implements SqlService {

  private final OxmSqlReader oxmSqlReader;
  private final SqlRegistry sqlRegistry;

  public OxmSqlService(SqlRegistry sqlRegistry, Unmarshaller unmarshaller, String sqlmapFile) {
    this.oxmSqlReader = new OxmSqlReader(unmarshaller, sqlmapFile);
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.oxmSqlReader.read(this.sqlRegistry);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    try {
      return this.sqlRegistry.findSql(key);
    } catch (SqlNotFoundException e) {
      throw new SqlRetrievalFailureException(e);
    }
  }

  private static class OxmSqlReader implements SqlReader {

    private final Unmarshaller unmarshaller;
    private final String sqlmapFile;

    private OxmSqlReader(Unmarshaller unmarshaller, String sqlmapFile) {
      this.unmarshaller = unmarshaller;
      this.sqlmapFile = sqlmapFile;
    }

    @Override
    public void read(SqlRegistry sqlRegistry) {
      try {
        Source source = new StreamSource(UserDao.class.getResourceAsStream(this.sqlmapFile));
        Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(source);

        for (SqlType sqlType : sqlmap.getSql()) {
          sqlRegistry.registerSql(sqlType.getKey(), sqlType.getValue());
        }
      } catch (IOException e) {
        throw new IllegalArgumentException(this.sqlmapFile + "을 가져올 수 없습니다.");
      }
    }
  }
}
