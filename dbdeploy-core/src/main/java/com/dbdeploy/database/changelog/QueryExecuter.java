package com.dbdeploy.database.changelog;

import java.sql.*;

public class QueryExecuter {
	private final Connection connection;
    private final String username;

    public QueryExecuter(String connectionString, String username, String password) throws SQLException {
        this.username = username;
        connection = DriverManager.getConnection(connectionString, username, password);
	}

	public ResultSet executeQuery(String sql) throws SQLException {
		Statement statement = connection.createStatement();
		return statement.executeQuery(sql);
	}

	public void execute(String sql) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.execute(sql);
		} finally {
			statement.close();
		}
	}

    public void execute(String sql, Object... params) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                statement.setObject(i+1, param);
            }
            statement.execute();
        } finally {
            statement.close();
        }
    }

    public boolean doesTableExist(String tableName) throws SQLException {
        ResultSet rsSame = null;
        ResultSet rsLower = null;
        ResultSet rsUpper = null;
        try {
            rsSame = connection.getMetaData().getTables(null, null, tableName, null);
            rsLower = connection.getMetaData().getTables(null, null, tableName.toLowerCase(), null);
            rsUpper = connection.getMetaData().getTables(null, null, tableName.toUpperCase(), null);

            return rsSame.next() || rsLower.next() || rsUpper.next();
        } finally {
            try {
                if (rsUpper != null) {
                    rsUpper.close();
                }
            } finally {
                try {
                    if (rsLower != null) {
                        rsLower.close();
                    }
                } finally {
                    if (rsSame != null) {
                        rsSame.close();
                    }
                }
            }
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

    public String getDatabaseUsername() {
        return username;
    }
}
