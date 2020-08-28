package dev.idion.springbook.learningtest.spring.data.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {RedissonSpringDataConfig.class, EmbeddedRedisConfig.class})
class EmbeddedRedisTest {

  @Autowired
  RedisTemplate<String, String> template;

  Map<String, String> sqlmap;

  @BeforeEach
  void setUp() {
    sqlmap = new HashMap<>();
    sqlmap.put("1", "SQL1");
    sqlmap.put("2", "SQL2");
    sqlmap.put("3", "SQL3");
    sqlmap.put("4", "SQL4");
    sqlmap.put("5", "SQL5");
  }

  @Test
  void saveAndReadTest() {
    for (Entry<String, String> entry : sqlmap.entrySet()) {
      template.opsForValue().append(entry.getKey(), entry.getValue());
    }

    for (Entry<String, String> entry : sqlmap.entrySet()) {
      assertThat(template.opsForValue().get(entry.getKey())).isEqualTo(entry.getValue());
    }
  }
}
