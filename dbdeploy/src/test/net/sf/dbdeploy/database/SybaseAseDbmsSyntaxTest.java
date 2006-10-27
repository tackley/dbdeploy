package net.sf.dbdeploy.database;

import java.io.File;

public class SybaseAseDbmsSyntaxTest extends AbstractDbmsSyntax {

	static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	static final String DB_HOST = "192.168.205.128";
	static final String DB_URL = "jdbc:jtds:sybase://" + DB_HOST + ":1276/dbdeploy";
	static final String USERNAME = "sa";
	static final String PASSWORD = "";
	static final File DIR = new File("." + System.getProperty("file.separator") + "example_scripts");
	static final String DELTA_SET = "All";
	static final String DBMS = "syb-ase";
	static final File OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "syb-ase-output-file.sql");
	static final File UNDO_OUTPUT_FILE = new File("." + System.getProperty("file.separator") 
			+ "build" + System.getProperty("file.separator") 
			+ "testoutput" + System.getProperty("file.separator")
			+ "syb-ase-output-undo-file.sql");

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
