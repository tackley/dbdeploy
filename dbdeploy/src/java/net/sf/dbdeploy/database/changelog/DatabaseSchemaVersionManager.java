package net.sf.dbdeploy.database.changelog;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for all interaction with the changelog table
 */
public class DatabaseSchemaVersionManager {

	private final String deltaSet;
	private final DbmsSyntax dbmsSyntax;
	private final QueryExecuter queryExecuter;

	public DatabaseSchemaVersionManager(String deltaSet, DbmsSyntax dbmsSyntax, QueryExecuter queryExecuter) {
		this.deltaSet = deltaSet;
		this.dbmsSyntax = dbmsSyntax;
		this.queryExecuter = queryExecuter;
	}

	public List<Integer> getAppliedChangeNumbers() throws SchemaVersionTrackingException {
		try {
			ResultSet rs = queryExecuter.execute("SELECT change_number FROM changelog WHERE delta_set = ? ORDER BY change_number", deltaSet);

			List<Integer> changeNumbers = new ArrayList<Integer>();

			while (rs.next()) {
				changeNumbers.add(rs.getInt(1));
			}

			return changeNumbers;
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
					+ e.getMessage(), e);
		}
	}


	public String generateDoDeltaFragmentHeader(ChangeScript changeScript) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("--------------- Fragment begins: %s ---------------\n", changeScript));
		builder.append(String.format(
				"INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description)" +
						" VALUES (%d, '%s', %s, %s, '%s')",
				changeScript.getId(),
				deltaSet,
				dbmsSyntax.generateTimestamp(),
				dbmsSyntax.generateUser(),
				changeScript.getDescription()));
		builder.append(dbmsSyntax.generateStatementDelimiter());
		builder.append('\n');
		builder.append(dbmsSyntax.generateCommit());
		builder.append('\n');

		return builder.toString();
	}

	public String generateDoDeltaFragmentFooter(ChangeScript changeScript) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format(
				"UPDATE changelog SET complete_dt = %s"
						+ " WHERE change_number = %d"
						+ " AND delta_set = '%s'",
				dbmsSyntax.generateTimestamp(),
				changeScript.getId(),
				deltaSet
		));
		builder.append(dbmsSyntax.generateStatementDelimiter());
		builder.append('\n');
		builder.append(dbmsSyntax.generateCommit());
		builder.append('\n');
		builder.append(String.format("--------------- Fragment ends: %s ---------------\n", changeScript));

		return builder.toString();
	}

	public String generateUndoDeltaFragmentFooter(ChangeScript changeScript) {
		StringBuilder builder = new StringBuilder();

		builder.append(String.format("DELETE FROM changelog "
				+ " WHERE change_number = %d"
				+ " AND delta_set = '%s'",
				changeScript.getId(), deltaSet));
		builder.append(dbmsSyntax.generateStatementDelimiter());
		builder.append('\n');
		builder.append(dbmsSyntax.generateCommit());
		builder.append('\n');

		builder.append(String.format("--------------- Fragment ends: %s ---------------\n", changeScript));
		return builder.toString();
	}
}
