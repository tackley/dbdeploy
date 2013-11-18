package com.dbdeploy;

import com.dbdeploy.appliers.DirectToDbApplier;
import com.dbdeploy.appliers.TemplateBasedApplier;
import com.dbdeploy.appliers.UndoTemplateBasedApplier;
import com.dbdeploy.database.DelimiterType;
import com.dbdeploy.database.LineEnding;
import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import com.dbdeploy.database.changelog.QueryExecuter;
import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.scripts.ChangeScriptRepository;
import com.dbdeploy.scripts.ClasspathScanner;
import com.dbdeploy.scripts.DirectoryScanner;
import com.dbdeploy.scripts.Scanner;

import java.io.File;
import java.io.PrintWriter;

public class DbDeploy {
	private String url;
	private String userid;
	private String password;
	private String encoding = "UTF-8";
	private File scriptdirectory;
    private String scriptPackage;
	private File outputfile;
	private File undoOutputfile;
	private LineEnding lineEnding = LineEnding.platform;
	private String dbms;
	private Long lastChangeToApply = Long.MAX_VALUE;
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

    public void setScriptPackage(String scriptPackage) {
        this.scriptPackage = scriptPackage;
    }

	public void setOutputfile(File outputfile) {
		this.outputfile = outputfile;
	}

	public void setDbms(String dbms) {
		this.dbms = dbms;
	}

	public void setLastChangeToApply(Long lastChangeToApply) {
		this.lastChangeToApply = lastChangeToApply;
	}

	public void setUndoOutputfile(File undoOutputfile) {
		this.undoOutputfile = undoOutputfile;
	}

	public void setChangeLogTableName(String changeLogTableName) {
		this.changeLogTableName = changeLogTableName;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setLineEnding(LineEnding lineEnding) {
		this.lineEnding = lineEnding;
	}

	public void go() throws Exception {
		System.err.println(getWelcomeString());

		validate();

		Class.forName(driver);

		QueryExecuter queryExecuter = new QueryExecuter(url, userid, password);

		DatabaseSchemaVersionManager databaseSchemaVersionManager =
				new DatabaseSchemaVersionManager(queryExecuter, changeLogTableName);

        Scanner finder;
        if (scriptPackage == null) {
            finder = new DirectoryScanner(encoding, scriptdirectory);
        } else {
            finder = new ClasspathScanner(scriptPackage, encoding);
        }
        ChangeScriptRepository changeScriptRepository = new ChangeScriptRepository(finder.getChangeScripts());

		ChangeScriptApplier doScriptApplier;

		if (outputfile != null) {
			doScriptApplier = new TemplateBasedApplier(
					new PrintWriter(outputfile, encoding), dbms,
					changeLogTableName, delimiter, delimiterType, getTemplatedir());
		} else {
			QueryStatementSplitter splitter = new QueryStatementSplitter();
			splitter.setDelimiter(getDelimiter());
			splitter.setDelimiterType(getDelimiterType());
			splitter.setOutputLineEnding(lineEnding);
			doScriptApplier = new DirectToDbApplier(queryExecuter, databaseSchemaVersionManager, splitter);
		}

		ChangeScriptApplier undoScriptApplier = null;

		if (undoOutputfile != null) {
			undoScriptApplier = new UndoTemplateBasedApplier(
				new PrintWriter(undoOutputfile), dbms, changeLogTableName, delimiter, delimiterType, templatedir);

		}

		Controller controller = new Controller(changeScriptRepository, databaseSchemaVersionManager, doScriptApplier, undoScriptApplier);

		controller.processChangeScripts(lastChangeToApply);

		queryExecuter.close();
	}

	private void validate() throws UsageException {
		checkForRequiredParameter(userid, "userid");
		checkForRequiredParameter(driver, "driver");
		checkForRequiredParameter(url, "url");
		if (scriptdirectory == null && scriptPackage == null) {
            throw new UsageException("scriptdirectory or scriptPackage must be set");
        }

		if (scriptdirectory != null && !scriptdirectory.isDirectory()) {
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

	public Long getLastChangeToApply() {
		return lastChangeToApply;
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

	public String getEncoding() {
		return encoding;
	}

	public LineEnding getLineEnding() {
		return lineEnding;
	}
}
