package org.pedro.rentacar.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class Database {

    private static Database instance;
    private String url;
    private String user;
    private String password;
    private String driver;

    private Database() throws SQLException {
        try {
            Properties props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("dbconfig.properties");
            props.load(is);

            url = props.getProperty("db.url");
            user = props.getProperty("db.user");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            Class.forName(driver);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    public static Database getInstance() throws SQLException {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
