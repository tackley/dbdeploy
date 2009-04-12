package com.dbdeploy.database.syntax;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import com.dbdeploy.database.syntax.*;

public class DbmsSyntaxFactoryTest {
	private final DbmsSyntaxFactory factory = new DbmsSyntaxFactory();

	@Test
	public void shouldGetCorrectSyntaxForSupportedTypes() throws Exception {
		assertThat(factory.createDbmsSyntax("ora"), is(OracleDbmsSyntax.class));
		assertThat(factory.createDbmsSyntax("ora-sqlplus"), is(OracleSqlPlusDbmsSyntax.class));
		assertThat(factory.createDbmsSyntax("hsql"), is(HsqlDbmsSyntax.class));
		assertThat(factory.createDbmsSyntax("syb-ase"), is(SybAseDbmsSyntax.class));
		assertThat(factory.createDbmsSyntax("mssql"), is(MsSqlDbmsSyntax.class));
		assertThat(factory.createDbmsSyntax("mysql"), is(MySQLDbmsSyntax.class));
	}

	@Test
	public void shouldThrowWhenSyntaxIncorrect() throws Exception {
		try {
			factory.createDbmsSyntax("sdfkjsdlkj");
			fail("expected exception");
		} catch (IllegalArgumentException ex) {
			assertEquals(
					"Supported dbms: hsql, mssql, mysql, ora, ora-sqlplus, syb-ase",
					ex.getMessage()
			);
		}
	}

	@Test
	public void shouldReturnListOfSyntaxNames() throws Exception {
		assertThat(factory.getSyntaxNames(),
				hasItems("hsql", "mssql", "mysql", "ora", "ora-sqlplus", "syb-ase"));
	}

}
