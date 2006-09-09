package net.sf.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;

public class DatabaseSchemaVersionManager {

	public static final String TABLE_NAME = "DatabaseConfiguration";

	private Connection connection;

	public DatabaseSchemaVersionManager(String connectionString, String username, String password) throws SQLException {
		connection = DriverManager.getConnection(connectionString, username, password);
	}

	public int getCurrentVersion() throws SchemaVersionTrackingException {
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT SchemaVersion FROM " + TABLE_NAME);

			if (!rs.next()) {
				statement.executeUpdate("INSERT INTO "+ TABLE_NAME + " VALUES (0)");
				statement.close();
				return 0;
			}

			return rs.getInt(1);
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve schema version from database because: "
					+ e.getMessage(), e);
		}

	}

	public String generateSqlToUpdateSchemaVersion(int versionNumber) {
		return "UPDATE " + TABLE_NAME + " SET SchemaVersion = " + versionNumber;
	}


}
