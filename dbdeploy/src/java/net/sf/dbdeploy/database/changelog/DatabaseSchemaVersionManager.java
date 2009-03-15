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
			public void writeContentTo(PrintWriter contentWriter) {
				contentWriter.printf("--------------- Fragment begins: %s ---------------", changeScript);
				contentWriter.println();
				contentWriter.printf(
						"INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description)" +
								" VALUES (%d, '%s', %s, %s, '%s')",
						changeScript.getId(),
						deltaSet,
						dbmsSyntax.generateTimestamp(),
						dbmsSyntax.generateUser(),
						changeScript.getDescription());
				contentWriter.append(dbmsSyntax.generateStatementDelimiter());
				contentWriter.println();
				contentWriter.append(dbmsSyntax.generateCommit());
				contentWriter.println();
			}
		};
	}

	public String generateDoDeltaFragmentFooter(final ChangeScript changeScript) {
		return collectIntoAStringWithSystemDependentLineTerminationCharacters(doDeltaFragementFooterContent(changeScript));
	}

	private ContentWriter doDeltaFragementFooterContent(final ChangeScript changeScript) {
		return new ContentWriter() {

			public void writeContentTo(PrintWriter contentWriter) {
				contentWriter.printf(
						"UPDATE changelog SET complete_dt = %s"
								+ " WHERE change_number = %d"
								+ " AND delta_set = '%s'",
						dbmsSyntax.generateTimestamp(),
						changeScript.getId(),
						deltaSet);
				contentWriter.append(dbmsSyntax.generateStatementDelimiter());
				contentWriter.println();

				contentWriter.append(dbmsSyntax.generateCommit());
				contentWriter.println();

				contentWriter.printf("--------------- Fragment ends: %s ---------------", changeScript);
				contentWriter.println();
			}
		};
	}

	public String generateUndoDeltaFragmentFooter(ChangeScript changeScript) {
		return collectIntoAStringWithSystemDependentLineTerminationCharacters(undoDeltaFragmentFooterContent(changeScript));
	}

	private ContentWriter undoDeltaFragmentFooterContent(final ChangeScript changeScript) {
		return new ContentWriter() {
			public void writeContentTo(PrintWriter contentWriter) {
				contentWriter.printf("DELETE FROM changelog "
						+ " WHERE change_number = %d"
						+ " AND delta_set = '%s'",
						changeScript.getId(), deltaSet);
				contentWriter.append(dbmsSyntax.generateStatementDelimiter());
				contentWriter.println();

				contentWriter.append(dbmsSyntax.generateCommit());
				contentWriter.println();

				contentWriter.printf("--------------- Fragment ends: %s ---------------", changeScript);
				contentWriter.println();
			}
		};
	}

	private String collectIntoAStringWithSystemDependentLineTerminationCharacters(ContentWriter collector) {
		StringWriter content = new StringWriter();
		PrintWriter contentWriter = new PrintWriter(content);
		try {
			collector.writeContentTo(contentWriter);
			contentWriter.flush();
			return content.toString();
		} finally {
			contentWriter.close();
		}
	}

	private interface ContentWriter {
		void writeContentTo(PrintWriter contentWriter);
	}
}
