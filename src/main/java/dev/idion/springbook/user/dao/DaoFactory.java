package dev.idion.springbook.user.dao;

public class DaoFactory {

  public UserDao userDao() {
    return new UserDao(connectionMaker());
  }

  public ConnectionMaker connectionMaker() {
    return new DConnectionMaker();
  }
}
