package org.EduardoMango.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:biblioteca.db";
    private static final DataSource datasource;

    static {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setConnectionInitSql("PRAGMA foreign_keys = ON;");
        datasource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return datasource.getConnection();
    }
}
