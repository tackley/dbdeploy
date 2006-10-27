package net.sf.dbdeploy.database;

import java.io.File;

public class OracleSqlPlusDbmsSyntaxTest extends AbstractDbmsSyntax {

	static final String DRIVER = "oracle.jdbc.OracleDriver";
	static final String DB_HOST = "192.168.205.128";
	static final String DB_URL = "jdbc:oracle:thin:@" + DB_HOST + ":1521:XE";
	static final String USERNAME = "smallaxe";
	static final String PASSWORD = "s3cur1ty";
	static final File DIR = new File("." + System.getProperty("file.separator") + "example_scripts");
	static final String DELTA_SET = "All";
	static final String DBMS = "ora-sqlplus";
	static final File OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "ora-sqlplus-output-file.sql");
	static final File UNDO_OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "ora-sqlplus-output-undo-file.sql");

	@Override
	protected String getDriver() {
		return DRIVER;
	}
	
	protected String getDbUrl() {
		return DB_URL;
	}
	
	protected String getUsername() {
		return USERNAME;
	}
	
	protected String getPassword() {
		return PASSWORD;
	}
	
	protected File getDir() {
		return DIR;
	}
	
	protected String getDeltaSet() {
		return DELTA_SET;
	}
	
	protected String getDbms() {
		return DBMS;
	}
	
	protected File getOutputFile() {
		return OUTPUT_FILE;
	}

	@Override
	protected File getUndoOutputFile() {
		return UNDO_OUTPUT_FILE;
	}

}
