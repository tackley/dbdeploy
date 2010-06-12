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
import com.dbdeploy.database.DelimiterType;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;

/**
 * Runs dbdeploy
 *
 * @goal update
 */
public class DatabaseUpdateMojo extends AbstractMojo {

    /**
     * Location of your scripts
     *
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Directory where change scripts reside
     *
     * @parameter
     * @required
     */
    private File dir;

    /**
     * Class name of database driver to use
     *
     * @parameter
     * @required
     */
    private String driver;

    /**
     * URL for database.
     *
     * @parameter
     * @required
     */
    private String url;

    /**
     * Password for database userid.
     *
     * @parameter
     */
    private String password;

    /**
     * Userid that will run the dbdeploy scripts.
     *
     * @parameter
     * @required
     */
    private String userid;

    /**
     * Output script path + name
     *
     * @parameter
     */
    private File outputfile;

    /**
     * String representing our DBMS (e.g. mysql, ora)
     *
     * @parameter
     */
    private String dbms;

    /**
     * Number of the last script to apply
     * 
     * @parameter expression="${dbdeploy.lastChange}"
     */
    private Long lastChangeToApply;

    /**
     * Undo script path + name
     *
     * @parameter
     */
    private File undoOutputfile;
    
    /**
     * Change log table name
     *
     * @parameter
     */
    private String changeLogTableName;

    /**
     * Statement delimiter
     *
     * @parameter
     */
    private String delimiter;

    /**
     * Statement delimiter type
     *
     * @parameter
     */
    private DelimiterType delimiterType;

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
            throw new MojoExecutionException("dbdeploy update failed", e);
        }
    }

    private DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = new DbDeploy();
        dbDeploy.setScriptdirectory(dir);
        dbDeploy.setDriver(driver);
        dbDeploy.setUrl(url);
        dbDeploy.setPassword(password);
        dbDeploy.setUserid(userid);

        if (outputfile != null) {
            dbDeploy.setOutputfile(outputfile);
        }

        if (dbms != null) {
            dbDeploy.setDbms(dbms);
        }

        if (undoOutputfile != null) {
            dbDeploy.setUndoOutputfile(undoOutputfile);
        }

        if (lastChangeToApply != null) {
            dbDeploy.setLastChangeToApply(lastChangeToApply);
        }

        if (changeLogTableName != null) {
            dbDeploy.setChangeLogTableName(changeLogTableName);
        }

        if (delimiter != null) {
            dbDeploy.setDelimiter(delimiter);
        }

        if (delimiterType != null) {
            dbDeploy.setDelimiterType(delimiterType);
        }

        if (templateDirectory != null) {
            dbDeploy.setTemplatedir(templateDirectory);
        }


        return dbDeploy;
    }
}
