package dev.idion.springbook.learningtest.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JUnitConfig.class)
class JUnitTest {

  static ApplicationContext contextObject = null;
  static Set<JUnitTest> testObjects = new HashSet<>();

  @Autowired
  ApplicationContext context;

  @Test
  void test1() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);

    assertThat(contextObject == null || contextObject == this.context).isTrue();
  }

  @Test
  void test2() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
    assertThat(contextObject).satisfiesAnyOf(
        contextObject -> assertThat(contextObject).as("실패이유").isNull(),
        contextObject -> assertThat(contextObject).isSameAs(this.context)
    );
  }

  @Test
  void test3() {
    assertThat(this).isNotIn(testObjects);
    assertThat(testObjects).doesNotContain(this);
    testObjects.add(this);
    assertThat(contextObject == null || contextObject == this.context).isTrue();
  }
}
