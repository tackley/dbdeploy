package net.sf.dbdeploy.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;

public class DatabaseSchemaVersionManager {

	public static final String TABLE_NAME = "changelog";

	private Connection connection;

	public DatabaseSchemaVersionManager(String connectionString, String username, String password) throws SQLException {
		connection = DriverManager.getConnection(connectionString, username, password);
	}

	public List<Integer> getAppliedChangeNumbers() throws SchemaVersionTrackingException {
		List<Integer> changeNumbers = new ArrayList<Integer>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT change_number FROM " + TABLE_NAME + " ORDER BY change_number");

			while (rs.next()) {
				changeNumbers.add(rs.getInt(1));
			}

			return changeNumbers;
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
					+ e.getMessage(), e);
		}
	}

	public String generateSqlToUpdateSchemaVersion(ChangeScript changeScript) {
		return "INSERT INTO " + TABLE_NAME + " (change_number, description)" +
			" VALUES (" + changeScript.getId() + ", '" + changeScript.getDescription() + "')";
	}
}
