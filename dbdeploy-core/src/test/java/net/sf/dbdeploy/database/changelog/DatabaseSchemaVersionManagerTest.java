package net.sf.dbdeploy.database.changelog;

import net.sf.dbdeploy.database.syntax.DbmsSyntax;
import net.sf.dbdeploy.scripts.ChangeScript;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DatabaseSchemaVersionManagerTest {
	private final DatabaseSchemaVersionManager schemaVersionManager
			= new DatabaseSchemaVersionManager("deltaSetName", new StubDbmsSyntax(), new StubQueryExecuter());

	@Mock
	private ResultSet expectedResultSet;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldUseQueryExecuterToReadInformationFromTheChangelogTable() throws Exception {
		when(expectedResultSet.next()).thenReturn(true, true, true, false);
		when(expectedResultSet.getInt(1)).thenReturn(5, 9, 12);

		final List<Integer> numbers = schemaVersionManager.getAppliedChangeNumbers();
		assertThat(numbers, hasItems(5, 9, 12));
	}

	// TODO: DatabaseSchemaVersionManager should only be involved with the SQL required to update and read the changelog table.
	//  All of the "fragment begins" stuff and the commit logic
	// should be the responsibility of the code that generates the script, not here

	@Test
	public void doFragmentHeaderShouldBeFormattedCorrectlyBasedOnSyntax() {
		String result = schemaVersionManager.generateDoDeltaFragmentHeader(new ChangeScript(99, "Some Description"));

		String expected = format("--------------- Fragment begins: #99 ---------------");

		assertEquals(expected, result);
	}

	@Test
	public void shouldGenerateDoFragmentFooter() {
		String result = schemaVersionManager.generateDoDeltaFragmentFooter(new ChangeScript(99, "Some Description"));

		String expected = format(
				"INSERT INTO changelog (change_number, delta_set, complete_dt, applied_by, description) VALUES (99, 'deltaSetName', (timestamp), (user), 'Some Description');%n" +
						"COMMIT;%n" +
						"--------------- Fragment ends: #99 ---------------%n");

		assertEquals(expected, result);
	}

	@Test
	public void shouldGenerateUndoStatementFooter() {
		String result = schemaVersionManager.generateUndoDeltaFragmentFooter(new ChangeScript(99, "Some Description"));

		String expected = format(
				"DELETE FROM changelog  WHERE change_number = 99 AND delta_set = 'deltaSetName';%n" +
						"COMMIT;%n" +
						"--------------- Fragment ends: #99 ---------------%n");

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

