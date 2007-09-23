package net.sf.dbdeploy.database.syntax;

public class OracleSqlPlusDbmsSyntax implements DbmsSyntax {

	public String generateScriptHeader() {
		StringBuilder builder = new StringBuilder();
		/* Halt the script on error. */
		builder.append("WHENEVER SQLERROR EXIT sql.sqlcode ROLLBACK\n");
		/* Disable '&' variable substitution. */
		builder.append("SET DEFINE OFF");
		return builder.toString();
	}

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER";
	}

	public String generateStatementDelimiter() {
		return ";";
	}

	public String generateCommit() {
		return "COMMIT" + generateStatementDelimiter();
	}

}
