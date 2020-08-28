package dev.idion.springbook.learningtest.spring.data.redis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

@Configuration
public class EmbeddedRedisConfig {

  private RedisServer redisServer;

  @PostConstruct
  public void redisServer() {
    this.redisServer = new RedisServer(6379);
    this.redisServer.start();
  }

  @PreDestroy
  public void stopRedis() {
    if (this.redisServer != null) {
      redisServer.stop();
    }
  }
}
