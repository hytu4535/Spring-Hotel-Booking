package com.hotel.dao;

import com.hotel.util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {

    protected Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    protected void close(AutoCloseable... resources) {
        DBConnection.close(resources);
    }
}
