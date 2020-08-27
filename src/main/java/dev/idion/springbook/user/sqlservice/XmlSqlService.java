package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {

  private final Map<String, String> sqlMap = new HashMap<>();
  @Autowired
  private SqlReader sqlReader;
  @Autowired
  private SqlRegistry sqlRegistry;
  private final String sqlmapFile;

  public XmlSqlService(String sqlmapFile) {
    this.sqlmapFile = sqlmapFile;
  }

  public void setSqlReader(SqlReader sqlReader) {
    this.sqlReader = sqlReader;
  }

  public void setSqlRegistry(SqlRegistry sqlRegistry) {
    this.sqlRegistry = sqlRegistry;
  }

  @PostConstruct
  public void loadSql() {
    this.sqlReader.read(this.sqlRegistry);
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    try {
      return this.sqlRegistry.findSql(key);
    } catch (SqlNotFoundException e) {
      throw new SqlRetrievalFailureException(e);
    }
  }

  @Override
  public void registerSql(String key, String sql) {
    this.sqlMap.put(key, sql);
  }

  @Override
  public String findSql(String key) throws SqlNotFoundException {
    String sql = this.sqlMap.get(key);
    if (sql == null) {
      throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
    }
    return sql;
  }

  @Override
  public void read(SqlRegistry sqlRegistry) {
    String contextPath = Sqlmap.class.getPackage().getName();
    try {
      JAXBContext context = JAXBContext.newInstance(contextPath);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
      Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
      for (SqlType sqlType : sqlmap.getSql()) {
        sqlRegistry.registerSql(sqlType.getKey(), sqlType.getValue());
      }
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }
}
