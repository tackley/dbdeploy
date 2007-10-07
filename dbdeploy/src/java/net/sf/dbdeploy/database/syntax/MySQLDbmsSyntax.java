package net.sf.dbdeploy.database.syntax;

public class MySQLDbmsSyntax extends DbmsSyntax {

	public String generateTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String generateUser() {
		return "USER()";
	}
}
