package com.taragana.nclt.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Basic Database Connection provider which looks for db.properties in current directory otherwise reads it from the jar file.
 * This provides connection pooling but doesn't provide advanced connection retru features beyond what is provided by HikariCP configuration
 */
public class DBConnection {
    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static HikariDataSource ds = null;

    static {
        File f = new File(DB_PROPERTIES_FILE);
        InputStream is = null;
        if (f.exists() && f.canRead()) {
            try {
                is = new FileInputStream(f);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            is = ClassLoader.getSystemResourceAsStream(DB_PROPERTIES_FILE);
        }
        Properties properties = new Properties();
        if (is != null) {
            try {
                properties.load(is);
                ds = new HikariDataSource(new HikariConfig(properties));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static Connection getConnection() throws SQLException {
        if (ds != null) return ds.getConnection();
        return null;
    }

}
