package com.dbdeploy;

import com.dbdeploy.exceptions.UsageException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.File;

import com.dbdeploy.DbDeploy;

public class AntTarget extends Task {
	private DbDeploy dbDeploy = new DbDeploy();

	private static String ANT_USAGE = "\n\nDbdeploy Ant Task Usage"
			+ "\n======================="
			+ "\n\n\t<dbdeploy"
			+ "\n\t\tuserid=\"[DATABASE USER ID]\" *"
			+ "\n\t\tpassword=\"[DATABASE USER ID PASSWORD]\""
			+ "\n\t\tdriver=\"[DATABASE DRIVER]\" *"
			+ "\n\t\turl=\"[DATABASE URL]\" *"
			+ "\n\t\tdbms=\"[YOUR DBMS]\" *"
			+ "\n\t\tdir=\"[YOUR SCRIPT FOLDER]\" *"
			+ "\n\t\toutputfile=\"[OUTPUT SCRIPT PATH + NAME]\" *"
			+ "\n\t\tmaxNumberToApply=\"[NUMBER OF THE LAST SCRIPT TO APPLY]\""
			+ "\n\t\tdeltaSet=\"[NAME OF DELTA SET TO BE APPLIED]\""
			+ "\n\t\tundoOutputfile=\"[UNDO SCRIPT PATH + NAME]\""
			+ "\n\t/>"
			+ "\n\n* - Indicates mandatory parameter";

	@Override
	public void execute() throws BuildException {
		try {
			dbDeploy.go();
		} catch (UsageException ex) {
			System.err.println(ANT_USAGE);
			throw new BuildException(ex.getMessage());
		} catch (Exception ex) {
			throw new BuildException(ex);
		}
	}

	public void setDir(File dir) {
		dbDeploy.setScriptdirectory(dir);
	}

	public void setDriver(String driver) {
		dbDeploy.setDriver(driver);
	}

	public void setUrl(String url) {
		dbDeploy.setUrl(url);
	}

	public void setPassword(String password) {
		dbDeploy.setPassword(password);
	}

	public void setUserid(String userid) {
		dbDeploy.setUserid(userid);
	}

	public void setOutputfile(File outputfile) {
		dbDeploy.setOutputfile(outputfile);
	}

	public void setDbms(String dbms) {
		dbDeploy.setDbms(dbms);
	}

	public void setLastChangeToApply(Integer maxNumberToApply) {
		dbDeploy.setLastChangeToApply(maxNumberToApply);
	}

	public void setDeltaSet(String deltaSet) {
		dbDeploy.setDeltaset(deltaSet);
	}

	public void setUndoOutputfile(File undoOutputfile) {
		dbDeploy.setUndoOutputfile(undoOutputfile);
	}

}

