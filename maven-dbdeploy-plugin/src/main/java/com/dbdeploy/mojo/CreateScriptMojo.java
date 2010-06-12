/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dbdeploy.mojo;

import com.dbdeploy.scripts.ChangeScriptCreator;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author jbogan
 *
 * @goal createScript
 */
public class CreateScriptMojo extends AbstractMojo {
    /**
     * Name suffix for the file that will be created (e.g. add_email_to_user_table).
     *
     * @parameter expression="${dbdeploy.script.name}" default-value="new_delta_script"
     * @required
     */
    private String name;

    /**
     * Directory where change scripts reside.
     *
     * @parameter
     * @required
     */
    private File dir;

    public void execute() throws MojoExecutionException {
        try {
            final ChangeScriptCreator changeScriptCreator = getConfiguredChangeScriptCreator();
            final File newChangeScript = changeScriptCreator.go();

            getLog().info("Created new change script:\n\t" + newChangeScript.getAbsolutePath());
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("create change script failed", e);
        }
    }

    private ChangeScriptCreator getConfiguredChangeScriptCreator() {
        final ChangeScriptCreator changeScriptCreator = new ChangeScriptCreator();
        changeScriptCreator.setScriptDescription(name);
        changeScriptCreator.setScriptDirectory(dir);

        return changeScriptCreator;
    }
}
