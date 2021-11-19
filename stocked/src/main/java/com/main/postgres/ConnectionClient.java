package com.main.postgres;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class ConnectionClient {

    Properties dbProperties = new Properties();

    private static final String url = "jdbc:postgresql://localhost/postgres";

    public static Connection createConnection() {

        Connection dbConn = null;
        try {
            // Create Properties object.
            Properties props = new Properties();

            String dbSettingsPropertyFile = "./database.properties";
            // Properties will use a FileReader object as input.
            FileReader fReader = new FileReader(dbSettingsPropertyFile);

            // Load jdbc related properties in above file.
            props.load(fReader);

            // Get each property value.
            String dbDriverClass = props.getProperty("db.driver.class");

            String dbConnUrl = props.getProperty("db.conn.url");

            String dbUserName = props.getProperty("db.user");

            String dbPassword = props.getProperty("db.password");

            Class.forName(dbDriverClass);

            dbConn = DriverManager.getConnection(dbConnUrl, dbUserName, dbPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbConn;
    }
}
