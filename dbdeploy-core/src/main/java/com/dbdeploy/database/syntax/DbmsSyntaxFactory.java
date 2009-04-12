package com.dbdeploy.database.syntax;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class DbmsSyntaxFactory {
	private Map<String, DbmsSyntax> available = new TreeMap<String, DbmsSyntax>();

	public DbmsSyntaxFactory() {
		available.put("ora", new OracleDbmsSyntax());
		available.put("ora-sqlplus", new OracleSqlPlusDbmsSyntax());
		available.put("hsql", new HsqlDbmsSyntax());
		available.put("syb-ase", new SybAseDbmsSyntax());
		available.put("mssql", new MsSqlDbmsSyntax());
		available.put("mysql", new MySQLDbmsSyntax());
	}

	public DbmsSyntax createDbmsSyntax(String dbms) {
		DbmsSyntax syntax = available.get(dbms);

		if (syntax == null) {
			throw new IllegalArgumentException("Supported dbms: " + buildListOfDbmsStrings());
		}

		return syntax;
	}

	private String buildListOfDbmsStrings() {
		StringBuilder content = new StringBuilder();

		for (String s : available.keySet()) {
			content.append(s);
			content.append(", ");
		}

		// remove the last comma
		content.setLength(content.length() - 2);

		return content.toString();
	}

	public Collection<String> getSyntaxNames() {
		return available.keySet();
	}
}
