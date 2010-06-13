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
import java.io.File;
import org.apache.maven.plugin.AbstractMojo;

/**
 * Abstract class that all dbdeploy database goals should extend.
 */
public abstract class AbstractDbDeployMojo extends AbstractMojo {
    /**
     * Full or relative path to the directory containing the delta scripts.
     *
     * @parameter
     * @required
     */
    protected File dir;

    /**
     * Specifies the jdbc driver.
     *
     * @parameter
     * @required
     */
    protected String driver;

    /**
     * Specifies the url of the database that the deltas are to be applied to. 
     *
     * @parameter
     * @required
     */
    protected String url;

    /**
     * The password of the dbms user who has permissions to select from the
     * schema version table.
     *
     * @parameter
     */
    protected String password;

    /**
     * The ID of a dbms user who has permissions to select from the schema
     * version table.
     *
     * @parameter
     * @required
     */
    protected String userid;

    /**
     * The name of the changelog table to use. Useful if you need to separate
     * DDL and DML when deploying to replicated environments. If not supplied
     * defaults to "changelog"
     *
     * @parameter
     */
    protected String changeLogTableName;

    /**
     * Delimiter to use to separate scripts into statements, if dbdeploy will
     * apply the scripts for you i.e. you haven't specified outputfile. Default ;
     *
     * @parameter
     */
    protected String delimiter;

    /**
     * Either normal: split on delimiter wherever it occurs or row  only split
     * on delimiter if it features on a line by itself. Default normal.
     *
     * @parameter
     */
    protected DelimiterType delimiterType;

    /**
     * The highest numbered delta script to apply.
     *
     * @parameter expression="${dbdeploy.lastChange}"
     */
    protected Long lastChangeToApply;

    protected DbDeploy getConfiguredDbDeploy() {
        DbDeploy dbDeploy = new DbDeploy();
        dbDeploy.setScriptdirectory(dir);
        dbDeploy.setDriver(driver);
        dbDeploy.setUrl(url);
        dbDeploy.setPassword(password);
        dbDeploy.setUserid(userid);

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

        return dbDeploy;
    }
}
