package com.kookykraftmc.api.global.sql;

import java.sql.*;

public class SQLConnection {

    private final String user;
    private final String database;
    private final String password;
    private final int port;
    private final String hostname;
    protected Connection connection;

    /**
     * @param hostname
     * @param port
     * @param database
     * @param username
     * @param password
     */
    public SQLConnection(String hostname, int port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
    }

    /**
     * @return
     */
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            return this.connection;
        }
        Class.forName("com.mysql.jdbc.Driver");
        this.connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + String.valueOf(this.port) + "/" + this.database, this.user, this.password);
        return this.connection;
    }

    /**
     * @return
     * @throws java.sql.SQLException
     */
    public boolean checkConnection() throws SQLException {
        return (this.connection != null) && (!this.connection.isClosed());
    }

    /**
     * @return
     */
    public Connection getConnection() {
        return this.connection;
    }

    public boolean closeConnection() throws SQLException {
        if (this.connection == null) {
            return false;
        }
        this.connection.close();
        return true;
    }

    /**
     * @param query
     * @return
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */
    public ResultSet querySQL(String query) throws SQLException, ClassNotFoundException {
        if (!checkConnection()) {
            openConnection();
        }
        Statement statement = this.connection.createStatement();

        return statement.executeQuery(query);
    }

    /**
     * @param query
     * @return
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */
    public int updateSQL(String query) throws SQLException, ClassNotFoundException {
        if (!checkConnection()) {
            openConnection();
        }
        Statement statement = this.connection.createStatement();

        return statement.executeUpdate(query);
    }

    /**
     * @param query
     * @return
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */
    public boolean executeSQL(String query) throws SQLException, ClassNotFoundException {
        if (!checkConnection()) {
            openConnection();
        }
        Statement statement = this.connection.createStatement();

        return statement.execute(query);
    }

    public String getUser() {
        return user;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

}
