package com.dbdeploy.database.syntax;

public class MsSqlDbmsSyntax extends DbmsSyntax {

	public String generateTimestamp() {
		return "getdate()";
	}

	public String generateUser() {
		return "user_name()";
	}

	public String generateStatementDelimiter() {
		return String.format("%nGO");
	}
}
