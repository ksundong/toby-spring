package dev.idion.springbook.user.domain;

public class User {

  String id;
  String name;
  String password;
  int login;
  int recommend;
  Level level;

  public User() {
  }

  public User(String id, String name, String password, int login, int recommend,
      Level level) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.login = login;
    this.recommend = recommend;
    this.level = level;
  }

  public void upgradeLevel() {
    Level nextLevel = this.level.nextLevel();
    if (nextLevel == null) {
      throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
    }
    this.level = nextLevel;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getLogin() {
    return login;
  }

  public void setLogin(int login) {
    this.login = login;
  }

  public int getRecommend() {
    return recommend;
  }

  public void setRecommend(int recommend) {
    this.recommend = recommend;
  }

  public Level getLevel() {
    return level;
  }

  public void setLevel(Level level) {
    this.level = level;
  }
}
