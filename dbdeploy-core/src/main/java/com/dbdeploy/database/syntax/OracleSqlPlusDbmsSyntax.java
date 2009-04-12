package com.dbdeploy.database.syntax;

public class OracleSqlPlusDbmsSyntax extends DbmsSyntax {

	public String generateScriptHeader() {
		return
				/* Halt the script on error. */
				String.format("WHENEVER SQLERROR EXIT sql.sqlcode ROLLBACK%n" +
//						 Disable '&' variable substitution.
						"SET DEFINE OFF");
	}

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER";
	}
}
