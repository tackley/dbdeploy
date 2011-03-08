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
import com.dbdeploy.DbDeploy;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Maven goal for creating the apply and undo scripts.
 *
 * @goal db-scripts
 */
public class CreateDatabaseScriptsMojo extends AbstractDbDeployMojo {

    /**
     * The name of the script that dbdeploy will output. Include a full
     * or relative path.
     *
     * @parameter
     * @required
     */
    private File outputfile;

    /**
     * String representing our DBMS (e.g. mysql, ora)
     *
     * @parameter
     * @required
     */
    private String dbms;

    /**
     * The name of the undo script that dbdeploy will output. Include a full
     * or relative path.
     *
     * @parameter
     * @required
     */
    private File undoOutputfile;

    /**
     * Directory for your template scripts, if not using built-in
     *
     * @parameter
     */
    private File templateDirectory;

    public void execute() throws MojoExecutionException {
        DbDeploy dbDeploy = getConfiguredDbDeploy();

        try {
            dbDeploy.go();
        } catch (Exception e) {
            getLog().error(e);
            throw new MojoExecutionException("dbdeploy change script create failed", e);
        }
    }

    @Override
    protected DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = super.getConfiguredDbDeploy();
        dbDeploy.setOutputfile(outputfile);
        dbDeploy.setUndoOutputfile(undoOutputfile);
        dbDeploy.setDbms(dbms);
        
        if (templateDirectory != null) {
            dbDeploy.setTemplatedir(templateDirectory);
        }

        return dbDeploy;
    }
}
