package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class JaxbXmlSqlReader implements SqlReader {

  private final Resource sqlmap;

  public JaxbXmlSqlReader(Resource sqlmap) {
    this.sqlmap = sqlmap;
  }

  @Override
  public void read(SqlRegistry sqlRegistry) {
    String contextPath = Sqlmap.class.getPackage().getName();
    try {
      JAXBContext context = JAXBContext.newInstance(contextPath);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      InputStream is = this.sqlmap.getInputStream();
      Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);
      for (SqlType sqlType : sqlmap.getSql()) {
        sqlRegistry.registerSql(sqlType.getKey(), sqlType.getValue());
      }
    } catch (JAXBException | IOException e) {
      throw new RuntimeException(e);
    }
  }
}
