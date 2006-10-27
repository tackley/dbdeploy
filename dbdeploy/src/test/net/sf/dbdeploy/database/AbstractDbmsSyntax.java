package net.sf.dbdeploy.database;

import java.io.File;

import net.sf.dbdeploy.AntTarget;
import junit.framework.TestCase;

public abstract class AbstractDbmsSyntax extends TestCase {
	
	public void testRunTheDamnThing() {
		String driver = getDriver();
		String url = getDbUrl();
		String username = getUsername();
		String password = getPassword();
		File dir = getDir();
		String deltaSet = getDeltaSet();
		String dbms = getDbms();
		File outputfile = getOutputFile();
		File undoOutputfile = getUndoOutputFile();
		
		AntTarget antTarget = new AntTarget();
		
		antTarget.setDriver(driver);
		antTarget.setUrl(url);
		antTarget.setUserid(username);
		antTarget.setPassword(password);
		antTarget.setDir(dir);
		antTarget.setDeltaSet(deltaSet);
		antTarget.setDbms(dbms);
		antTarget.setOutputfile(outputfile);
		antTarget.setUndoOutputfile(undoOutputfile);
		
		antTarget.execute();
		
		assertNotNull(antTarget);
	}

	protected abstract String getDriver();
	protected abstract String getDbUrl();
	protected abstract String getUsername();
	protected abstract String getPassword();
	protected abstract File getDir();
	protected abstract String getDeltaSet();
	protected abstract String getDbms();
	protected abstract File getOutputFile();
	protected abstract File getUndoOutputFile();
}
