package net.tackley.dbdeploy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchemaVersion {

	public static final String TABLE_NAME = "DatabaseConfiguration";

	private Connection connection;
		
	public DatabaseSchemaVersion(String connectionString, String username, String password) throws SQLException {		
		connection = DriverManager.getConnection(connectionString, username, password);
	}
	int getCurrentVersion() throws SchemaVersionTrackingException {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT SchemaVersion FROM " + TABLE_NAME);
			
			if (!rs.next()) {
				throw new SchemaVersionTrackingException("Could not retrieve schema version from database because: table exists but is empty\n");
			}

			return rs.getInt(1);
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve schema version from database because: " + e.getMessage(), e);
		}
		
	}
	void setCurrentVersion(int versionNumber) throws SchemaVersionTrackingException {
		Statement statement;
		try {
			statement = connection.createStatement();
			int rowsAffected = statement.executeUpdate("UPDATE " + TABLE_NAME + " SET SchemaVersion = " + versionNumber);
			statement.close();
			
			if (rowsAffected != 1) {
				throw new SchemaVersionTrackingException("Could not update schema version in the database because: sql reports that " + rowsAffected + " rows were affected: expected 1 to be affected");
			}
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not update schema version in the database because: " + e.getMessage(), e);
		}
	}

}

