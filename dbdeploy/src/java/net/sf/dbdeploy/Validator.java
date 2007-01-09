package net.sf.dbdeploy;

import org.apache.tools.ant.BuildException;

public class Validator {
	private String usage;
	
	private static String ERROR_MESSAGE_HEADER = "Dbdeploy parameter validation error\n\n";
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

	private static String COMMAND_LINE_USAGE = "\n\n\nDbdeploy Command Line Usage"
		+ "\n==========================="
		+ "\n\n\tTODO - SPECIFY USAGE HERE";
	
	public void validate(String userid, String driver, String url, String dbms, String dir, String outputFile) {
		String errorMessage;
		
		if (userid == null || userid == "") {
			throw new BuildException(constructErrorMessage("Userid expected"));
		}

		if (driver == null || driver == "") {
			throw new BuildException(constructErrorMessage("Driver expected"));
		}
		
		if (url == null || url == "") {
			throw new BuildException(constructErrorMessage("Url expected"));
		}
		
		if ( !dbms.equals("ora") && !dbms.equals("ora-sqlplus") && !dbms.equals("hsql") && !dbms.equals("syb-ase") && !dbms.equals("mssql") && !dbms.equals("mysql") ) {
			throw new BuildException(constructErrorMessage("Unknown DBMS: " + dbms + "\n\nAllowed values:\nora - Oracle\nhsql - Hypersonic SQL\nsyb-ase - Sybase ASE\nmssql - Microsoft SQL Server\nmysql - MySQL database"));
		}
		
		if (dir == null || dir == "") {
			throw new BuildException(constructErrorMessage("Dir expected"));
		}
		
		if (outputFile == null || outputFile == "") {
			throw new BuildException(constructErrorMessage("Output file expected"));
		}
	}
	
	private String constructErrorMessage(String customMessagePortion) {
		
		if (usage == "ant") {
			return ERROR_MESSAGE_HEADER + customMessagePortion + ANT_USAGE;
		} else if (usage == "commandline") {
			return ERROR_MESSAGE_HEADER + customMessagePortion + COMMAND_LINE_USAGE;				
		} else {
			throw new BuildException("Unexpected usage!");
		}		
	}
	
	public void setUsage(String usage) {
		this.usage = usage;
	}

}
