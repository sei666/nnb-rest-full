package ru.icerebro.nnbrestfull.database.JDBC.interfaces;

import java.sql.Connection;

public interface MyConPool {

    Connection getConnection();
    void freeConnection(Connection connection);
}
