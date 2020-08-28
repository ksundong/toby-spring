package dev.idion.springbook.learningtest.jdk.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.jupiter.api.Test;

class JaxbTest {

  @Test
  void readSqlmap() throws JAXBException {
    String contextPath = Sqlmap.class.getPackage().getName();
    JAXBContext context = JAXBContext.newInstance(contextPath);
    Unmarshaller unmarshaller = context.createUnmarshaller();

    Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream(
        "/sqlmap.xml"));
    List<SqlType> sqlList = sqlmap.getSql();

    assertThat(sqlList.size()).isEqualTo(3);
    assertThat(sqlList.get(0).getKey()).isEqualTo("add");
    assertThat(sqlList.get(0).getValue()).isEqualTo("insert");
    assertThat(sqlList.get(1).getKey()).isEqualTo("get");
    assertThat(sqlList.get(1).getValue()).isEqualTo("select");
    assertThat(sqlList.get(2).getKey()).isEqualTo("delete");
    assertThat(sqlList.get(2).getValue()).isEqualTo("delete");
  }
}
