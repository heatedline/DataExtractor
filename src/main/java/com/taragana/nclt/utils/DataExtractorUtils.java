package com.taragana.nclt.utils;

import java.sql.SQLException;

/**
 * Contains common utility methods that is needed for certain implementations of requirements.
 * @Author Supratim
 */
public class DataExtractorUtils {

    /**
     * When JDBC encounters an error during an interaction with a data source, it throws an instance of SQLException as opposed to Exception.
     * (A data source in this context represents the database to which a Connection object is connected.)
     * @param ex The input SQLException
     * outputs the SQLState, error code, error description, and cause (if there is one) contained in the SQLException as well as any other exception chained to it:
     */
    public static void printSQLException(SQLException ex) {

        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                if (!ignoreSQLException(((SQLException) e).getSQLState())) {
                    e.printStackTrace(System.err);
                    System.err.println("SQLState: " +
                            ((SQLException)e).getSQLState());

                    System.err.println("Error Code: " +
                            ((SQLException)e).getErrorCode());

                    System.err.println("Message: " + e.getMessage());

                    Throwable t = ex.getCause();
                    while(t != null) {
                        System.out.println("Cause: " + t);
                        t = t.getCause();
                    }
                }
            }
        }
    }

    /**
     * Instead of outputting SQLException information, you could instead first retrieve the SQLState then process the SQLException accordingly.
     * @param sqlState
     * @return true if the SQLState is equal to code 42Y55
     */
    private static boolean ignoreSQLException(String sqlState) {

        if (sqlState == null) {
            System.out.println("The SQL state is not defined!");
            return false;
        }

        // X0Y32: Jar file already exists in schema
        if (sqlState.equalsIgnoreCase("X0Y32"))
            return true;

        // 42Y55: Table already exists in schema
        if (sqlState.equalsIgnoreCase("42Y55"))
            return true;

        return false;
    }

}
