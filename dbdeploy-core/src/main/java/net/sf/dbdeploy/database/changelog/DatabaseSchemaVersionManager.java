package net.sf.dbdeploy.database.changelog;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;

import java.io.PrintWriter;
import java.io.StringWriter;
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

			rs.close();

			return changeNumbers;
		} catch (SQLException e) {
			throw new SchemaVersionTrackingException("Could not retrieve change log from database because: "
					+ e.getMessage(), e);
		}
	}


	public String generateDoDeltaFragmentHeader(ChangeScript changeScript) {
		return collectIntoAStringWithSystemDependentLineTerminationCharacters(doDeltaFragmentHeaderContent(changeScript));
	}

	private ContentWriter doDeltaFragmentHeaderContent(final ChangeScript changeScript) {
		return new ContentWriter() {
			public void writeContentTo(PrintWriter printWriter) {
				printWriter.printf("--------------- Fragment begins: %s ---------------", changeScript);
			}
		};
	}

	public String generateDoDeltaFragmentFooter(final ChangeScript changeScript) {
		return collectIntoAStringWithSystemDependentLineTerminationCharacters(doDeltaFragementFooterContent(changeScript));
	}

	private ContentWriter doDeltaFragementFooterContent(final ChangeScript changeScript) {
		return new ContentWriter() {

			public void writeContentTo(PrintWriter printWriter) {
				printWriter.printf(
						"INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description)" +
								" VALUES (%d, '%s', %s, %s, '%s')",
						changeScript.getId(),
						deltaSet,
						dbmsSyntax.generateTimestamp(),
						dbmsSyntax.generateUser(),
						changeScript.getDescription());
				printWriter.append(dbmsSyntax.generateStatementDelimiter());
				printWriter.println();

				printWriter.append(dbmsSyntax.generateCommit());
				printWriter.println();

				printWriter.printf("--------------- Fragment ends: %s ---------------", changeScript);
				printWriter.println();
			}
		};
	}

	public String generateUndoDeltaFragmentFooter(ChangeScript changeScript) {
		return collectIntoAStringWithSystemDependentLineTerminationCharacters(undoDeltaFragmentFooterContent(changeScript));
	}

	private ContentWriter undoDeltaFragmentFooterContent(final ChangeScript changeScript) {
		return new ContentWriter() {
			public void writeContentTo(PrintWriter printWriter) {
				printWriter.printf("DELETE FROM changelog "
						+ " WHERE change_number = %d"
						+ " AND delta_set = '%s'",
						changeScript.getId(), deltaSet);
				printWriter.append(dbmsSyntax.generateStatementDelimiter());
				printWriter.println();

				printWriter.append(dbmsSyntax.generateCommit());
				printWriter.println();

				printWriter.printf("--------------- Fragment ends: %s ---------------", changeScript);
				printWriter.println();
			}
		};
	}

	private String collectIntoAStringWithSystemDependentLineTerminationCharacters(ContentWriter collector) {
		StringWriter content = new StringWriter();
		PrintWriter printWriter = new PrintWriter(content);
		try {
			collector.writeContentTo(printWriter);
			printWriter.flush();
			return content.toString();
		} finally {
			printWriter.close();
		}
	}

	private interface ContentWriter {
		void writeContentTo(PrintWriter printWriter);
	}
}
