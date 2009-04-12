package com.dbdeploy.database.syntax;

public class OracleDbmsSyntax extends DbmsSyntax {

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER";
	}

}
