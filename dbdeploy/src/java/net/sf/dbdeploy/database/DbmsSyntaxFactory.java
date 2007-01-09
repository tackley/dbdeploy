package net.sf.dbdeploy.database;

public class DbmsSyntaxFactory {

	private final String dbms;

	public DbmsSyntaxFactory(String dbms) {
		this.dbms = dbms;
	}

	public DbmsSyntax createDbmsSyntax() {
		if (dbms.equals("ora")) {
			return new OracleDbmsSyntax();
		} else if (dbms.equals("ora-sqlplus")) {
			return new OracleSqlPlusDbmsSyntax();
		} else if (dbms.equals("hsql")) {
			return new HsqlDbmsSyntax();
		} else if (dbms.equals("syb-ase")) {
			return new SybAseDbmsSyntax();
		} else if (dbms.equals("mssql")) {
			return new MsSqlDbmsSyntax();
		} else if (dbms.equals("mysql")) {
			return new MySQLDbmsSyntax();	
		} else {
			throw new IllegalArgumentException("Supported dbms: ora, hsql, syb-ase, mssql, mysql");
		}
	}
}
