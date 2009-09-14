package com.dbdeploy.database.changelog;

import com.dbdeploy.AppliedChangesProvider;
import com.dbdeploy.database.syntax.DbmsSyntax;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.scripts.ChangeScript;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for all interaction with the changelog table
 */
public class DatabaseSchemaVersionManager implements AppliedChangesProvider {

	private final DbmsSyntax dbmsSyntax;
	private final QueryExecuter queryExecuter;
    private final String changeLogTableName;

    public DatabaseSchemaVersionManager(DbmsSyntax dbmsSyntax, QueryExecuter queryExecuter, String changeLogTableName) {
		this.dbmsSyntax = dbmsSyntax;
		this.queryExecuter = queryExecuter;
        this.changeLogTableName = changeLogTableName;
    }

	public List<Integer> getAppliedChanges() {
		try {
			ResultSet rs = queryExecuter.executeQuery(
					"SELECT change_number FROM " + changeLogTableName + "  ORDER BY change_number");

			List<Integer> changeNumbers = new ArrayList<Integer>();

			while (rs.next()) {
				changeNumbers.add(rs.getInt(1));
			}

			rs.close();

			return changeNumbers;
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
					+ e.getMessage(), e);
		}
	}

	public String getChangelogInsertSql(ChangeScript script) {
		return String.format(
			"INSERT INTO " + changeLogTableName + " (change_number, complete_dt, applied_by, description)%n" +
					" VALUES (%d, %s, %s, '%s')",
			script.getId(),
			dbmsSyntax.generateTimestamp(),
			dbmsSyntax.generateUser(),
			script.getDescription());
	}

	public String getChangelogDeleteSql(ChangeScript script) {
		return String.format(
			"DELETE FROM " + changeLogTableName + " WHERE change_number = %d",
				script.getId());
	}
}
