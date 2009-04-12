package com.dbdeploy;

import com.dbdeploy.appliers.ApplyMode;
import com.dbdeploy.appliers.PrintStreamApplier;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.database.syntax.DbmsSyntax;
import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.scripts.ChangeScriptRepository;
import com.dbdeploy.scripts.DirectoryScanner;

import java.io.*;

public class DbDeploy {
	private String url;
	private String userid;
	private String password;
	private File scriptdirectory;
	private File outputfile;
	private File undoOutputfile;
	private String dbms;
	private Integer lastChangeToApply = Integer.MAX_VALUE;
	private String deltaset = "Main";
	private String driver;

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setScriptdirectory(File scriptdirectory) {
		this.scriptdirectory = scriptdirectory;
	}

	public void setOutputfile(File outputfile) {
		this.outputfile = outputfile;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public void setLastChangeToApply(Integer lastChangeToApply) {
		this.lastChangeToApply = lastChangeToApply;
	}

	public void setDeltaset(String deltaset) {
		this.deltaset = deltaset;
	}

	public void setUndoOutputfile(File undoOutputfile) {
		this.undoOutputfile = undoOutputfile;
	}

	public void go() throws Exception {
        System.err.println(getWelcomeString());

		validate();

		Class.forName(driver);

		DbmsSyntax dbmsSyntax = DbmsSyntax.createFor(dbms);

		QueryExecuter queryExecuter = new QueryExecuter(url, userid, password);

		DatabaseSchemaVersionManager databaseSchemaVersion =
				new DatabaseSchemaVersionManager(deltaset, dbmsSyntax, queryExecuter);

		ChangeScriptRepository changeScriptRepository =
				new ChangeScriptRepository(new DirectoryScanner().getChangeScriptsForDirectory(scriptdirectory));

		ChangeScriptApplier doScriptApplier =
				new PrintStreamApplier(ApplyMode.DO, new PrintStream(outputfile), dbmsSyntax, databaseSchemaVersion);

		ChangeScriptApplier undoScriptApplier = null;

		if (undoOutputfile != null) {
			undoScriptApplier =
					new PrintStreamApplier(ApplyMode.UNDO, new PrintStream(undoOutputfile), dbmsSyntax, databaseSchemaVersion);

		}

		Controller controller = new Controller(changeScriptRepository, databaseSchemaVersion, doScriptApplier, undoScriptApplier);

		controller.processChangeScripts(lastChangeToApply);

		queryExecuter.close();
	}

	private void validate() throws UsageException {
		checkForRequiredParameter(userid, "userid");
		checkForRequiredParameter(driver, "driver");
		checkForRequiredParameter(url, "url");
		checkForRequiredParameter(dbms, "dbms");
		checkForRequiredParameter(outputfile, "outputfile");
		checkForRequiredParameter(scriptdirectory, "dir");

		if (scriptdirectory == null || !scriptdirectory.isDirectory()) {
			throw new UsageException("Script directory must point to a valid directory");
		}
	}

	private void checkForRequiredParameter(String parameterValue, String parameterName) throws UsageException {
		if (parameterValue == null || parameterValue.length() == 0) {
			UsageException.throwForMissingRequiredValue(parameterName);
		}
	}

	private void checkForRequiredParameter(Object parameterValue, String parameterName) throws UsageException {
		if (parameterValue == null) {
			UsageException.throwForMissingRequiredValue(parameterName);
		}
	}

	private PrintStream createUndoOutputPrintStream(File undoOutputFile) throws FileNotFoundException {
		if (undoOutputFile != null) {
			return new PrintStream(undoOutputFile);
		} else {
			return null;
		}
	}

	public String getUserid() {
		return userid;
	}

	public String getUrl() {
		return url;
	}

	public String getPassword() {
		return password;
	}

	public File getScriptdirectory() {
		return scriptdirectory;
	}

	public File getOutputfile() {
		return outputfile;
	}

	public File getUndoOutputfile() {
		return undoOutputfile;
	}

	public String getDbms() {
		return dbms;
	}

	public Integer getLastChangeToApply() {
		return lastChangeToApply;
	}

	public String getDeltaset() {
		return deltaset;
	}

	public String getDriver() {
		return driver;
	}

    public String getWelcomeString() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("welcome.txt");
        try {
            return new BufferedReader(new InputStreamReader(stream)).readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
