package net.sf.dbdeploy.database.syntax;


public interface DbmsSyntax {
	public String generateScriptHeader();
	
	public String generateTimestamp();
	
	public String generateUser();
	
	public String generateStatementDelimiter();
	
	public String generateCommit();
}
