package com.dbdeploy.database.changelog;

import java.sql.*;

public class QueryExecuter {
	private final Connection connection;

	public QueryExecuter(String connectionString, String username, String password) throws SQLException {
		connection = DriverManager.getConnection(connectionString, username, password);
	}

	public ResultSet execute(String sql, String parameter) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(sql);
		statement.setString(1, parameter);
		return statement.executeQuery();
	}

	public void execute(String sql) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.execute(sql);
		} finally {
			statement.close();
		}
	}

	public void close() throws SQLException {
		connection.close();
	}

	public void setAutoCommit(boolean autoCommitMode) throws SQLException {
		connection.setAutoCommit(autoCommitMode);
	}

	public void commit() throws SQLException {
		connection.commit();
	}
}
