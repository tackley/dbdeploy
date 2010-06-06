package com.dbdeploy;

import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.scripts.ChangeScriptCreator;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task that will create a new dbdeploy
 * change script with a timestamp and
 * specified file name.
 *
 * @author jbogan
 */
public class CreateChangeScriptTarget extends Task {
    final ChangeScriptCreator changeScriptCreator = new ChangeScriptCreator();

    private static String ANT_USAGE = "\n\nDbdeploy Create Script Ant Task Usage"
            + "\n======================="
            + "\n\n\t<createscript"
            + "\n\t\tdir=\"[YOUR SCRIPT FOLDER]\" *"
            + "\n\t\tname=\"[BRIEF SCRIPT DESCRIPTION]\""
            + "\n\t/>"
            + "\n\n* - Indicates mandatory parameter";

    @Override
    public void execute() throws BuildException {
        try {
            changeScriptCreator.go();
        } catch (UsageException ex) {
            System.err.println(ANT_USAGE);
            throw new BuildException(ex.getMessage());
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }

    public void setName(final String name) {
        changeScriptCreator.setScriptDescription(name);
    }

    public void setDir(final File dir) {
        changeScriptCreator.setScriptDirectory(dir);
    }
}
