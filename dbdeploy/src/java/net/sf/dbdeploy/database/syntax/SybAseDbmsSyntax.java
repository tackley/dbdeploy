package net.sf.dbdeploy.database.syntax;

public class SybAseDbmsSyntax implements DbmsSyntax {

	public String generateScriptHeader() {
		return "";
	}

	public String generateTimestamp() {
		return "getdate()";
	}

	public String generateUser() {
		return "user_name()";
	}

	public String generateStatementDelimiter() {
		return "\nGO";
	}

	public String generateCommit() {
		return "COMMIT" + generateStatementDelimiter();
	}


}
