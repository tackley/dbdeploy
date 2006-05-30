package net.tackley.dbdeploy;

import java.io.File;
import java.io.PrintStream;

import net.tackley.dbdeploy.database.DatabaseSchemaVersionManager;
import net.tackley.dbdeploy.exceptions.DbDeployException;
import net.tackley.dbdeploy.scripts.ChangeScriptRepositoryFactory;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class AntTarget extends Task  {
	
	private String driver;
	private String url;
	private String userid;
	private String password;
	private File dir;
	private File outputfile;

	public void setDir(File dir) {
		this.dir = dir;
	}

	@Override
	public void execute() throws BuildException {

		try {

			Class.forName(driver);

			Output output = new Output();
			DatabaseSchemaVersionManager databaseSchemaVersion = new DatabaseSchemaVersionManager(url, userid, password);
			ChangeScriptRepositoryFactory repositoryFactory = new ChangeScriptRepositoryFactory();

			PrintStream outputPrintStream = new PrintStream(outputfile);

			ChangeScriptExecuter changeScriptExecuter = new ChangeScriptExecuter(outputPrintStream);
			Controller controller = new Controller(output, databaseSchemaVersion, repositoryFactory,changeScriptExecuter);
			controller.applyScriptToGetChangesUpToLatestVersion(dir);

			outputPrintStream.close();

		} catch (DbDeployException ex) {
			System.err.println(ex.getMessage());
			throw new BuildException(ex);
		}
		catch (Exception ex) {
			System.err.println("Failed to apply changes: " + ex);
			ex.printStackTrace();
			throw new BuildException(ex);
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
	
}

