package dev.redelegends.parkour.database.interfaces;

import dev.redelegends.parkour.database.DataBase;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseInterface<T extends DataBase> {
    void setupDataBase();
    Connection getConnection() throws ClassNotFoundException, SQLException;
    void createTable(String table);
    void closeConnection();
}
