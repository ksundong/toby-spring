package dev.idion.springbook.user.sqlservice;

import dev.idion.springbook.user.dao.UserDao;
import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class XmlSqlService implements SqlService {

  private final Map<String, String> sqlMap;

  public XmlSqlService() {
    this.sqlMap = new HashMap<>();

    String contextPath = Sqlmap.class.getPackage().getName();
    try {
      JAXBContext context = JAXBContext.newInstance(contextPath);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      InputStream is = UserDao.class.getResourceAsStream("/userdao-sqlmap.xml");
      Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(is);

      for (SqlType sqlType : sqlmap.getSql()) {
        sqlMap.put(sqlType.getKey(), sqlType.getValue());
      }
    } catch (JAXBException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getSql(String key) throws SqlRetrievalFailureException {
    String sql = sqlMap.get(key);
    if (sql == null) {
      throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
    }
    return sql;
  }
}
