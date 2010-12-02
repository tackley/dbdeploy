package com.dbdeploy;

import java.io.File;

import com.dbdeploy.appliers.ChangeScriptApplier;
import com.dbdeploy.appliers.DirectToDbApplier;
import com.dbdeploy.appliers.TemplateBasedApplier;
import com.dbdeploy.database.DelimiterType;
import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.scripts.ChangeScriptRepository;
import com.dbdeploy.scripts.DirectoryScanner;

public class DbDeploy {
	private String url;
	private String userid;
	private String password;
	private File scriptdirectory;
	private File outputfile;
	private File undoOutputfile;
	private String dbms;
	private Integer targetVersion = Integer.MAX_VALUE; // based on the target version we decide if we need to upgrade or downgrade the database 
	private String driver;
	private String changeLogTableName = "changelog";
	private String delimiter = ";";
	private DelimiterType delimiterType = DelimiterType.normal;
	private File templatedir;

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

	public void setTargetVersion(Integer targetVersion) {
		this.targetVersion = targetVersion;
	}

	public void setUndoOutputfile(File undoOutputfile) {
		this.undoOutputfile = undoOutputfile;
	}

	public void setChangeLogTableName(String changeLogTableName) {
		this.changeLogTableName = changeLogTableName;
	}

	public void go() throws Exception {
		System.err.println(getWelcomeString());
		validate();
		Class.forName(driver);

		QueryExecuter queryExecuter = new QueryExecuter(url, userid, password);

		DatabaseSchemaVersionManager databaseSchemaVersionManager =
				new DatabaseSchemaVersionManager(queryExecuter, changeLogTableName);

		ChangeScriptRepository changeScriptRepository =
				new ChangeScriptRepository(new DirectoryScanner().getChangeScriptsForDirectory(scriptdirectory));

		ChangeScriptApplier doScriptApplier;

		if (outputfile != null) {
			doScriptApplier = new TemplateBasedApplier(outputfile, undoOutputfile, 
			        dbms, changeLogTableName, getTemplatedir());
		} else {
			QueryStatementSplitter splitter = new QueryStatementSplitter();
			splitter.setDelimiter(getDelimiter());
			splitter.setDelimiterType(getDelimiterType());
			doScriptApplier = new DirectToDbApplier(queryExecuter, databaseSchemaVersionManager, splitter);
		}

		Controller controller = new Controller(changeScriptRepository, databaseSchemaVersionManager, doScriptApplier);

		controller.processChangeScripts(targetVersion);

		queryExecuter.close();
	}

	private void validate() throws UsageException {
		checkForRequiredParameter(userid, "userid");
		checkForRequiredParameter(driver, "driver");
		checkForRequiredParameter(url, "url");
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

	public Integer getTargetVersion() {
		return targetVersion;
	}

	public String getDriver() {
		return driver;
	}

	public void setTemplatedir(File templatedir) {
		this.templatedir = templatedir;
	}

	public File getTemplatedir() {
		return templatedir;
	}

	public String getChangeLogTableName() {
		return changeLogTableName;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public DelimiterType getDelimiterType() {
		return delimiterType;
	}


	public void setDelimiterType(DelimiterType delimiterType) {
		this.delimiterType = delimiterType;
	}

	public String getWelcomeString() {
        String version = getClass().getPackage().getImplementationVersion();
        return "dbdeploy " + version;
	}
}
