package dev.idion.springbook.learningtest.spring.oxm;

import static org.assertj.core.api.Assertions.assertThat;

import dev.idion.springbook.user.sqlservice.jaxb.SqlType;
import dev.idion.springbook.user.sqlservice.jaxb.Sqlmap;
import java.io.IOException;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OxmTestConfig.class)
class OxmTest {

  @Autowired
  Unmarshaller unmarshaller;

  @Test
  void unmarshallSqlMap() throws IOException {
    Source xmlSource = new StreamSource(getClass().getResourceAsStream(
        "/sqlmap.xml"));

    Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);

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
