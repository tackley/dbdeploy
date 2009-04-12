package com.dbdeploy.database.changelog;

import java.sql.*;

public class QueryExecuter {
	private final Connection connection;

	public QueryExecuter(String connectionString, String username, String password) throws SQLException {
		connection = DriverManager.getConnection(connectionString, username, password);
	}

	public ResultSet execute(String sql, String parameter) throws SQLException {
		PreparedStatement statement = getConnection().prepareStatement(sql);
		statement.setString(1, parameter);
		return statement.executeQuery();
	}

	private Connection getConnection() throws SQLException {
		return connection;
	}

	public void close() throws SQLException {
		connection.close();
	}
}
