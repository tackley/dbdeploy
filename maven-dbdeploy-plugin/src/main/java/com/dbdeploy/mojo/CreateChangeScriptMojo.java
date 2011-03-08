package com.dbdeploy.mojo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import com.dbdeploy.scripts.ChangeScriptCreator;
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Maven goal for creating a new timestamped dbdeploy change script.
 *
 * @goal change-script
 */
public class CreateChangeScriptMojo extends AbstractMojo {
    /**
     * Name suffix for the file that will be created (e.g. add_email_to_user_table).
     *
     * @parameter expression="${dbdeploy.script.name}" default-value="new_change_script"
     * @required
     */
    private String name;

    /**
     * Directory where change scripts reside.
     *
     * @parameter expression="${dbdeploy.scriptdirectory}" default-value="${project.src.directory}/main/sql"
     * @required
     */
    private File scriptdirectory;

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
        changeScriptCreator.setScriptDirectory(scriptdirectory);

        return changeScriptCreator;
    }
}
