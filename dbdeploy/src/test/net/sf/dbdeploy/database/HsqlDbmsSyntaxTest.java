package net.sf.dbdeploy.database;

import java.io.File;

public class HsqlDbmsSyntaxTest extends AbstractDbmsSyntax {

	static final String DRIVER = "org.hsqldb.jdbcDriver";
	static final String DB_HOST = "localhost";
	static final String DB_URL = "jdbc:hsqldb:hsql://" + DB_HOST + "/xdb";
	static final String USERNAME = "sa";
	static final String PASSWORD = "";
	static final File DIR = new File("." + System.getProperty("file.separator") + "example_scripts");
	static final String DELTA_SET = "All";
	static final String DBMS = "hsql";
	static final File OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "hsql-output-file.sql");
	static final File UNDO_OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "hsql-output-undo-file.sql");

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
	
	protected File getUndoOutputFile() {
		return UNDO_OUTPUT_FILE;
	}

}
