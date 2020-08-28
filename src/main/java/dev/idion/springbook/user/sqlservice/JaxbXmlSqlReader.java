package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.springframework.stereotype.Component;

@Component
public class JaxbXmlSqlReader implements SqlReader {

  private final String sqlmapFile;

  public JaxbXmlSqlReader(String sqlmapFile) {
    this.sqlmapFile = sqlmapFile;
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
