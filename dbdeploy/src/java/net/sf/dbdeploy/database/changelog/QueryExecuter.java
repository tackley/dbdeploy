package net.sf.dbdeploy.database.changelog;

import java.sql.*;

public class QueryExecuter {
	private final String connectionString;
	private final String username;
	private final String password;

	public QueryExecuter(String connectionString, String username, String password) {
		this.connectionString = connectionString;
		this.username = username;
		this.password = password;
	}

	public ResultSet execute(String sql, String parameter) throws SQLException {
		PreparedStatement statement = getConnection().prepareStatement(sql);
		statement.setString(1, parameter);
		return statement.executeQuery();
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionString, username, password);
	}
}
