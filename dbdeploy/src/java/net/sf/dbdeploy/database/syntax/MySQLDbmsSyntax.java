package net.sf.dbdeploy.database.syntax;

public class MySQLDbmsSyntax implements DbmsSyntax {

	public String generateScriptHeader() {
		return "";
	}

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER()";
	}

	public String generateStatementDelimiter() {
		return ";";
	}

	public String generateCommit() {
		return "COMMIT" + generateStatementDelimiter();
	}

}
