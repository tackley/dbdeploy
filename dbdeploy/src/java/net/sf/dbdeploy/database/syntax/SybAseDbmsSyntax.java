package net.sf.dbdeploy.database.syntax;

public class SybAseDbmsSyntax extends DbmsSyntax {

	public String generateTimestamp() {
		return "getdate()";
	}

	public String generateUser() {
		return "user_name()";
	}

	public String generateStatementDelimiter() {
		return "\nGO";
	}

}
