package com.dbdeploy.database.syntax;

public class HsqlDbmsSyntax extends DbmsSyntax {

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER()";
	}

	@Override
	public String generateBeginTransaction() {
		// HSQL doesn't support explicit transactions
		// See http://hsqldb.sourceforge.net/doc/guide/ch09.html
		return "";
	}
}
