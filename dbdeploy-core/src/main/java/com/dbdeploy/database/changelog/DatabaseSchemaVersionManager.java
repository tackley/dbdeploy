package com.dbdeploy.database.changelog;

import com.dbdeploy.AppliedChangesProvider;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.exceptions.SchemaVersionTrackingException;
import com.dbdeploy.database.syntax.DbmsSyntax;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for all interaction with the changelog table
 */
public class DatabaseSchemaVersionManager implements AppliedChangesProvider {

	private final String deltaSet;
	private final DbmsSyntax dbmsSyntax;
	private final QueryExecuter queryExecuter;

	public DatabaseSchemaVersionManager(String deltaSet, DbmsSyntax dbmsSyntax, QueryExecuter queryExecuter) {
		this.deltaSet = deltaSet;
		this.dbmsSyntax = dbmsSyntax;
		this.queryExecuter = queryExecuter;
	}

	@Override
	public List<Integer> getAppliedChanges() {
		try {
			ResultSet rs = queryExecuter.execute("SELECT change_number FROM changelog WHERE delta_set = ? ORDER BY change_number", deltaSet);

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

	public String getChangelogUpdateSql(ChangeScript script) {
		return String.format(
			"INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description)%n" +
					" VALUES (%d, '%s', %s, %s, '%s')",
			script.getId(),
			deltaSet,
			dbmsSyntax.generateTimestamp(),
			dbmsSyntax.generateUser(),
			script.getDescription());
	}

	public String getChangelogDeleteSql(ChangeScript script) {
		return String.format(
			"DELETE FROM changelog WHERE change_number = %d AND delta_set = '%s'",
				script.getId(), deltaSet);
	}
}
