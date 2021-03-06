package dev.idion.springbook.learningtest.spring.data.redis;

import java.io.IOException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedissonSpringDataConfig {

  @Bean
  public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
    return new RedissonConnectionFactory(redisson);
  }

  @Bean(destroyMethod = "shutdown")
  public RedissonClient redisson(@Value("classpath:/redisson.yaml") Resource configFile)
      throws IOException {
    Config config = Config.fromYAML(configFile.getInputStream());
    return Redisson.create(config);
  }

  @Bean
  public RedisTemplate<String, String> redisTemplate(RedissonConnectionFactory factory) {
    return new StringRedisTemplate(factory);
  }
}
