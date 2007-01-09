package net.sf.dbdeploy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.database.DbmsSyntax;
import net.sf.dbdeploy.database.DbmsSyntaxFactory;
import net.sf.dbdeploy.exceptions.DbDeployException;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntTarget extends Task  {
	
	private String driver;
	private String url;
	private String userid;
	private String password;
	private File dir;
	private File outputfile;
	private String dbms;
	private Integer lastChangeToApply = Integer.MAX_VALUE;
	private String deltaSet = "Main";
	private File undoOutputfile;

	public void setDir(File dir) {
		this.dir = dir;
	}

	@Override
	public void execute() throws BuildException {
		
		try {
			
			Validator validator = new Validator();
			validator.setUsage("ant");
			validator.validate(userid, driver, url, dbms, dir.getAbsolutePath(), outputfile.getAbsolutePath());

			PrintStream outputPrintStream = new PrintStream(outputfile);
			PrintStream undoOutputPrintStream = createUndoOutputPrintStream(undoOutputfile);

			Class.forName(driver); 
			
			DbmsSyntaxFactory factory = new DbmsSyntaxFactory(dbms);
			DbmsSyntax dbmsSyntax = factory.createDbmsSyntax();

			DatabaseSchemaVersionManager databaseSchemaVersion = 
					new DatabaseSchemaVersionManager(url, userid, password, dbmsSyntax, deltaSet); 
			
			ToPrintSteamDeployer toPrintSteamDeployer = new ToPrintSteamDeployer(databaseSchemaVersion, dir, outputPrintStream, dbmsSyntax, undoOutputPrintStream);
			toPrintSteamDeployer.doDeploy(lastChangeToApply);

			outputPrintStream.close();
			if (undoOutputPrintStream != null) {
				undoOutputPrintStream.close();
			}
			

		} catch (DbDeployException ex) {
			System.err.println(ex.getMessage());
			throw new BuildException(ex);
		}
		catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			//ex.printStackTrace();
			System.err.println("Stack Trace:");
			ex.printStackTrace(System.err);
			throw new BuildException(ex);
		}

	}
	
	private PrintStream createUndoOutputPrintStream(File undoOutputFile) throws FileNotFoundException {
		if (undoOutputFile != null) {
			return new PrintStream(undoOutputFile);
		}
		else {
			return null;
		}
	}


	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setOutputfile(File outputfile) {
		this.outputfile = outputfile;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public void setLastChangeToApply(Integer maxNumberToApply) {
		this.lastChangeToApply = maxNumberToApply;
	}

	public void setDeltaSet(String deltaSet) {
		this.deltaSet = deltaSet;
	}

	public void setUndoOutputfile(File undoOutputfile) {
		this.undoOutputfile = undoOutputfile;
	}
	
}

