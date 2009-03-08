package net.sf.dbdeploy.database.changelog;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.scripts.ChangeScript;
import static org.easymock.EasyMock.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseSchemaVersionManagerTest {
	private final DatabaseSchemaVersionManager schemaVersionManager
			= new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), new StubQueryExecuter());
	private ResultSet expectedResultSet = createMock(ResultSet.class);

	@Test
	public void shouldUseQueryExecuterToReadInformationFromTheChangelogTable() throws Exception {
		expect(expectedResultSet.next()).andReturn(true);
		expect(expectedResultSet.getInt(1)).andReturn(5);
		expect(expectedResultSet.next()).andReturn(true);
		expect(expectedResultSet.getInt(1)).andReturn(9);
		expect(expectedResultSet.next()).andReturn(true);
		expect(expectedResultSet.getInt(1)).andReturn(12);
		expect(expectedResultSet.next()).andReturn(false);

		replay(expectedResultSet);
		final List<Integer> numbers = schemaVersionManager.getAppliedChangeNumbers();
		assertThat(numbers, hasItems(5, 9, 12));
	}

	// TODO: DatabaseSchemaVersionManager should only be involved with the SQL required to update and read the changelog table.
	//  All of the "fragment begins" stuff and the commit logic
	// should be the responsibility of the code that generates the script, not here

	@Test
	public void doFragmentHeaderShouldBeFormattedCorrectlyBasedOnSyntax() {
		String result = schemaVersionManager.generateDoDeltaFragmentHeader(new ChangeScript(99, "Some Description"));

		String expected = "--------------- Fragment begins: #99 ---------------\n" +
				"INSERT INTO changelog (change_number, delta_set, start_dt, applied_by, description) " +
				"VALUES (99, 'deltaSetName', (timestamp), (user), 'Some Description');\n" +
				"COMMIT;\n";

		assertEquals(expected, result);
	}

	@Test
	public void shouldGenerateDoFragmentFooter() {
		String result = schemaVersionManager.generateDoDeltaFragmentFooter(new ChangeScript(99, "Some Description"));

		String expected =
				"UPDATE changelog SET complete_dt = (timestamp) " +
						"WHERE change_number = 99 AND delta_set = 'deltaSetName';\n" +
						"COMMIT;\n" +
						"--------------- Fragment ends: #99 ---------------\n";

		assertEquals(expected, result);
	}

	@Test
	public void shouldGenerateUndoStatementFooter() {
		String result = schemaVersionManager.generateUndoDeltaFragmentFooter(new ChangeScript(99, "Some Description"));

		String expected =
				"DELETE FROM changelog  WHERE change_number = 99 AND delta_set = 'deltaSetName';\n" +
						"COMMIT;\n" +
						"--------------- Fragment ends: #99 ---------------\n";

		assertEquals(expected, result);

	}

	private class StubDbmsSyntax extends DbmsSyntax {
		public String generateTimestamp() {
			return "(timestamp)";
		}

		public String generateUser() {
			return "(user)";
		}
	}

	private class StubQueryExecuter extends QueryExecuter {

		public StubQueryExecuter() {
			super(null, null, null);
		}

		public ResultSet execute(String sql, String parameter) throws SQLException {
			return expectedResultSet;
		}
	}
}

