package com.main.postgres;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public abstract class ConnectionClient {

    Properties dbProperties = new Properties();

    public static Connection createConnection() {

        Connection dbConn = null;
        try {
            // Create Properties object.
            Properties props = new Properties();

            File dbSettingsPropertyFile = new File("./stocked/src/main/java/com/main/postgres/database.properties");

            // Properties will use a FileReader object as input.
            FileReader fReader = new FileReader(dbSettingsPropertyFile);
            // Load jdbc related properties in above file.
            props.load(fReader);

            // Get each property value.
            String dbDriverClass = props.getProperty("db.datasource.driver.class");

            props.setProperty("user", props.getProperty("db.datasource.user"));
            props.setProperty("password", props.getProperty("db.datasource.password"));
            Class.forName(dbDriverClass);
            String url = props.getProperty("db.datasource.url");
            dbConn = DriverManager.getConnection(url ,props);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dbConn;
    }
}
